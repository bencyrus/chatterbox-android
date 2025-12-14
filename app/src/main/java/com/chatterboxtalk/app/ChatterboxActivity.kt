package com.chatterboxtalk.app

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.gson.Gson
import com.chatterboxtalk.core.config.AppEnvironment
import com.chatterboxtalk.core.networking.APIClient
import com.chatterboxtalk.core.security.TokenManager
import com.chatterboxtalk.features.auth.repositories.PostgrestAuthRepository
import com.chatterboxtalk.features.auth.usecases.LoginWithMagicTokenUseCase
import com.chatterboxtalk.features.auth.usecases.LogoutUseCase
import com.chatterboxtalk.features.auth.usecases.RequestMagicLinkUseCase
import com.chatterboxtalk.features.auth.viewmodel.AuthViewModel
import com.chatterboxtalk.ui.views.LoginView
import com.chatterboxtalk.ui.views.RootTabView

class ChatterboxActivity : ComponentActivity() {
    private lateinit var tokenManager: TokenManager
    private lateinit var env: AppEnvironment
    private var tokenListener: SharedPreferences.OnSharedPreferenceChangeListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenManager = TokenManager(this)
        env = AppEnvironment(this)

        setContent {
            var loggedIn by remember { mutableStateOf(tokenManager.hasValidAccessToken()) }
            if (tokenListener == null) {
                tokenListener = tokenManager.addAccessTokenListener { hasToken ->
                    loggedIn = hasToken
                }
            }

            val client = APIClient(env, tokenManager, tokenManager, Gson())
            val repo = PostgrestAuthRepository(client, env, Gson())
            val logout = LogoutUseCase(tokenManager)
            val requestMagic = RequestMagicLinkUseCase(repo, tokenManager)
            val loginWithMagic = LoginWithMagicTokenUseCase(repo, tokenManager)
            val vm = AuthViewModel(this@ChatterboxActivity, env, logout, requestMagic, loginWithMagic)

            if (loggedIn) {
                RootTabView(tokenManager)
            } else {
                LoginView(vm)
            }
        }
        handleIntent(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        tokenListener?.let { tokenManager.removeListener(it) }
        tokenListener = null
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val data: Uri? = intent.data
        if (data != null && env.isValidMagicLink(data)) {
            val client = APIClient(env, tokenManager, tokenManager, Gson())
            val repo = PostgrestAuthRepository(client, env, Gson())
            val logout = LogoutUseCase(tokenManager)
            val requestMagic = RequestMagicLinkUseCase(repo, tokenManager)
            val loginWithMagic = LoginWithMagicTokenUseCase(repo, tokenManager)
            val vm = AuthViewModel(this, env, logout, requestMagic, loginWithMagic)
            vm.handleIncomingMagicToken(data)
        }
    }
}
