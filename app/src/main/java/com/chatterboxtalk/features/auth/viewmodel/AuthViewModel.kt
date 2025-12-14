package com.chatterboxtalk.features.auth.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatterboxtalk.core.config.AppEnvironment
import com.chatterboxtalk.features.auth.repositories.AuthError
import com.chatterboxtalk.features.auth.usecases.LoginWithMagicTokenUseCase
import com.chatterboxtalk.features.auth.usecases.LogoutUseCase
import com.chatterboxtalk.features.auth.usecases.RequestMagicLinkUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.ceil

/**
 * Android mirror of iOS `AuthViewModel.swift`.
 * Manages auth state with cooldown logic, cooldown persistence, and error handling.
 */
class AuthViewModel(
    context: Context,
    private val env: AppEnvironment,
    private val logout: LogoutUseCase,
    private val requestMagicLinkUC: RequestMagicLinkUseCase,
    private val loginWithMagicTokenUC: LoginWithMagicTokenUseCase,
) : ViewModel() {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "AuthViewModel", 
        Context.MODE_PRIVATE
    )

    private val _identifier = MutableStateFlow("")
    val identifier: StateFlow<String> = _identifier

    private val _isRequesting = MutableStateFlow(false)
    val isRequesting: StateFlow<Boolean> = _isRequesting

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private val _isShowingErrorAlert = MutableStateFlow(false)
    val isShowingErrorAlert: StateFlow<Boolean> = _isShowingErrorAlert

    private val _errorAlertTitle = MutableStateFlow("")
    val errorAlertTitle: StateFlow<String> = _errorAlertTitle

    private val _errorAlertMessage = MutableStateFlow("")
    val errorAlertMessage: StateFlow<String> = _errorAlertMessage

    private val _errorAlertLinkURL = MutableStateFlow<Uri?>(null)
    val errorAlertLinkURL: StateFlow<Uri?> = _errorAlertLinkURL

    private val _cooldownSecondsRemaining = MutableStateFlow(0)
    val cooldownSecondsRemaining: StateFlow<Int> = _cooldownSecondsRemaining

    private var cooldownTask: Job? = null
    private var cooldownExpiryDate: Long? = null

    companion object {
        private const val COOLDOWN_EXPIRY_KEY = "AuthViewModel.magicLinkCooldownExpiry"
    }

    init {
        restoreCooldownIfNeeded()
    }

    fun updateIdentifier(value: String) {
        _identifier.value = value
    }

    fun requestMagicLink() {
        val id = _identifier.value.trim()
        if (id.isEmpty()) {
            presentSignInError("errors.missing_identifier")
            return
        }
        if (_cooldownSecondsRemaining.value > 0) {
            return
        }
        _isRequesting.value = true
        viewModelScope.launch {
            try {
                val isImmediateLogin = requestMagicLinkUC.execute(id)
                if (!isImmediateLogin) {
                    // Normal flow: start cooldown only for non-reviewer accounts
                startCooldown(env.magicLinkCooldownSeconds)
                }
                // If isImmediateLogin is true, token manager updated and user will be logged in
            } catch (e: Exception) {
                when (e) {
                    is AuthError.AccountDeleted -> {
                        // Surface the server-provided message so support URL can be configured via secrets
                        presentSignInError(e.message)
                    }
                    else -> {
                presentSignInError("errors.request_failed")
                    }
                }
            } finally {
                _isRequesting.value = false
            }
        }
    }

    fun handleIncomingMagicToken(uri: Uri) {
        if (!env.isValidMagicLink(uri)) return
        val token = uri.getQueryParameter("token") ?: return
        viewModelScope.launch {
            try {
                loginWithMagicTokenUC.execute(token)
            } catch (e: Exception) {
                when (e) {
                    is AuthError.InvalidMagicLink -> {
                        presentSignInError("errors.invalid_magic_link")
                    }
                    is AuthError.AccountDeleted -> {
                        presentSignInError(e.message)
                    }
                    else -> {
                        presentSignInError("errors.request_failed")
                }
                }
            }
        }
    }

    fun dismissErrorAlert() {
        _isShowingErrorAlert.value = false
    }

    fun logout() {
        logout.execute()
    }

    private fun presentSignInError(message: String) {
        _errorMessage.value = message
        _errorAlertTitle.value = "errors.sign_in_error_title"
        _errorAlertMessage.value = message
        _errorAlertLinkURL.value = extractFirstURL(message)
        _isShowingErrorAlert.value = true
    }

    private fun extractFirstURL(text: String): Uri? {
        val httpsIndex = text.indexOf("https://")
        if (httpsIndex == -1) return null
        
        val substringFromURL = text.substring(httpsIndex)
        val urlToken = substringFromURL.split(Regex("\\s+|\n")).firstOrNull() ?: return null
        
        return try {
            Uri.parse(urlToken)
        } catch (e: Exception) {
            null
        }
    }

    private fun startCooldown(seconds: Int) {
        cooldownTask?.cancel()

        if (seconds <= 0) {
            _cooldownSecondsRemaining.value = 0
            cooldownExpiryDate = null
            prefs.edit().remove(COOLDOWN_EXPIRY_KEY).apply()
            return
        }

        val expiry = System.currentTimeMillis() + (seconds * 1000L)
        cooldownExpiryDate = expiry
        prefs.edit().putLong(COOLDOWN_EXPIRY_KEY, expiry).apply()

        // Set initial value immediately so UI updates without waiting 1s
        _cooldownSecondsRemaining.value = ceil((expiry - System.currentTimeMillis()) / 1000.0).toInt().coerceAtLeast(0)

        cooldownTask = viewModelScope.launch {
            while (true) {
                delay(1_000)
                val remaining = ceil((expiry - System.currentTimeMillis()) / 1000.0).toInt().coerceAtLeast(0)
                _cooldownSecondsRemaining.value = remaining

                if (remaining == 0) {
                    cooldownExpiryDate = null
                    prefs.edit().remove(COOLDOWN_EXPIRY_KEY).apply()
                    break
                }
            }
        }
    }

    /**
     * Restores an in-progress cooldown based on the last stored expiry time.
     */
    private fun restoreCooldownIfNeeded() {
        val timestamp = prefs.getLong(COOLDOWN_EXPIRY_KEY, 0L)
        if (timestamp == 0L) return

        val remaining = ceil((timestamp - System.currentTimeMillis()) / 1000.0).toInt()

        if (remaining > 0) {
            startCooldown(remaining)
        } else {
            _cooldownSecondsRemaining.value = 0
            cooldownExpiryDate = null
            prefs.edit().remove(COOLDOWN_EXPIRY_KEY).apply()
        }
    }

    override fun onCleared() {
        super.onCleared()
        cooldownTask?.cancel()
        cooldownTask = null
    }
}

