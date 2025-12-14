package com.chatterboxtalk.App

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
import com.chatterboxtalk.Core.Config.AppEnvironment
import com.chatterboxtalk.Core.Networking.APIClient
import com.chatterboxtalk.Core.Security.TokenManager
import com.chatterboxtalk.Features.Auth.Repositories.PostgrestAuthRepository
import com.chatterboxtalk.Features.Auth.UseCases.LoginWithMagicTokenUseCase
import com.chatterboxtalk.Features.Auth.UseCases.RequestMagicLinkUseCase
import com.chatterboxtalk.Features.Auth.ViewModel.AuthViewModel
import com.chatterboxtalk.UI.Views.LoginView
import com.chatterboxtalk.UI.Views.RootTabView

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
            val requestMagic = RequestMagicLinkUseCase(repo)
            val loginWithMagic = LoginWithMagicTokenUseCase(repo, tokenManager)
            val vm = AuthViewModel(env, requestMagic, loginWithMagic)

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
            val requestMagic = RequestMagicLinkUseCase(repo)
            val loginWithMagic = LoginWithMagicTokenUseCase(repo, tokenManager)
            val vm = AuthViewModel(env, requestMagic, loginWithMagic)
            vm.handleIncomingMagicToken(data)
        }
    }
}
