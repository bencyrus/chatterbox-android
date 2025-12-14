package com.chatterboxtalk.Features.Auth

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.Gson
import com.chatterboxtalk.Core.Config.AppEnvironment
import com.chatterboxtalk.Core.Networking.HTTPClient
import com.chatterboxtalk.Features.Auth.Repositories.PostgrestAuthRepository
import com.chatterboxtalk.Features.Auth.UseCases.LoginWithMagicTokenUseCase
import com.chatterboxtalk.Features.Auth.UseCases.RequestMagicLinkUseCase
import com.chatterboxtalk.Features.Auth.ViewModel.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthViewModelCooldownTest {

    @Test
    fun requestMagicLink_startsCooldown_andPreventsImmediateReRequest() = runBlocking {
        val context =
            androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        // Use the real AppEnvironment; no need to subclass it in this test.
        val env = AppEnvironment(context)
        val fakeClient = object : HTTPClient {
            override suspend fun <T : Any> postJson(
                path: String,
                body: T
            ): Pair<String, java.net.HttpURLConnection> {
                val url = java.net.URL("https://example.com")
                val conn = (url.openConnection() as java.net.HttpURLConnection)
                return Pair("{}", conn)
            }
        }
        val repo = PostgrestAuthRepository(fakeClient, env, Gson())
        val requestUC = RequestMagicLinkUseCase(repo)
        val loginUC = LoginWithMagicTokenUseCase(
            repo,
            tokenSink = object : io.glovee.chatterbox.Core.Security.TokenSink {
                override fun updateTokens(tokens: io.glovee.chatterbox.Core.Security.AuthTokens) {}
                override fun clearTokens() {}
            })
        val vm = AuthViewModel(env, requestUC, loginUC)

        vm.updateIdentifier("user@example.com")
        vm.requestMagicLink()

        // After request, cooldown should be > 0 quickly
        delay(100)
        val cooling = vm.cooldownSeconds.value
        assertTrue(cooling > 0)

        // Another request should not reset cooldown and should not throw
        vm.requestMagicLink()
        val stillCooling = vm.cooldownSeconds.value
        assertTrue(stillCooling <= cooling)
    }
}


