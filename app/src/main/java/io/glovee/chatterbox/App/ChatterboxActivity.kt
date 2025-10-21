package io.glovee.chatterbox.App

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.content.SharedPreferences
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.gson.Gson
import io.glovee.chatterbox.Core.Config.AppEnvironment
import io.glovee.chatterbox.Core.Networking.APIClient
import io.glovee.chatterbox.Core.Security.TokenManager
import io.glovee.chatterbox.Features.Auth.Repositories.PostgrestAuthRepository
import io.glovee.chatterbox.Features.Auth.UseCases.LoginWithMagicTokenUseCase
import io.glovee.chatterbox.Features.Auth.UseCases.RequestMagicLinkUseCase
import io.glovee.chatterbox.Features.Auth.ViewModel.AuthViewModel
import io.glovee.chatterbox.UI.Views.RootTabView
import io.glovee.chatterbox.UI.Views.LoginView
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember

class ChatterboxActivity: ComponentActivity() {
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
