package io.glovee.chatterbox.Features.Auth.ViewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.glovee.chatterbox.Core.Config.AppEnvironment
import io.glovee.chatterbox.Features.Auth.Repositories.AuthError
import io.glovee.chatterbox.Features.Auth.UseCases.LoginWithMagicTokenUseCase
import io.glovee.chatterbox.Features.Auth.UseCases.RequestMagicLinkUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val env: AppEnvironment,
    private val requestMagicLink: RequestMagicLinkUseCase,
    private val loginWithMagicToken: LoginWithMagicTokenUseCase,
) : ViewModel() {

    private val _identifier = MutableStateFlow("")
    val identifier: StateFlow<String> = _identifier

    private val _isRequesting = MutableStateFlow(false)
    val isRequesting: StateFlow<Boolean> = _isRequesting

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private val _cooldownSeconds = MutableStateFlow(0)
    val cooldownSeconds: StateFlow<Int> = _cooldownSeconds

    private var cooldownJob: Job? = null

    fun updateIdentifier(value: String) {
        _identifier.value = value
    }

    fun requestMagicLink() {
        val id = _identifier.value.trim()
        if (id.isEmpty()) {
            _errorMessage.value = "errors.missing_identifier"
            return
        }
        if (_cooldownSeconds.value > 0) {
            return
        }
        _errorMessage.value = ""
        _isRequesting.value = true
        viewModelScope.launch {
            try {
                requestMagicLink.execute(id)
                startCooldown(env.magicLinkCooldownSeconds)
            } catch (e: Exception) {
                _errorMessage.value = "errors.request_failed"
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
                loginWithMagicToken.execute(token)
            } catch (e: Exception) {
                _errorMessage.value = if (e is AuthError.InvalidMagicLink) {
                    "errors.invalid_magic_link"
                } else {
                    "errors.request_failed"
                }
            }
        }
    }

    private fun startCooldown(seconds: Int) {
        cooldownJob?.cancel()
        _cooldownSeconds.value = seconds.coerceAtLeast(0)
        cooldownJob = viewModelScope.launch {
            while (_cooldownSeconds.value > 0) {
                delay(1_000)
                _cooldownSeconds.value = (_cooldownSeconds.value - 1).coerceAtLeast(0)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        cooldownJob?.cancel()
        cooldownJob = null
    }
}
