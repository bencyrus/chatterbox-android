package com.chatterboxtalk.Features.Auth

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.Gson
import com.chatterboxtalk.Core.Config.AppEnvironment
import com.chatterboxtalk.Core.Networking.HTTPClient
import com.chatterboxtalk.Core.Security.AuthTokens
import com.chatterboxtalk.Features.Auth.Repositories.AuthError
import com.chatterboxtalk.Features.Auth.Repositories.PostgrestAuthRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PostgrestAuthRepositoryTest {

    @Test
    fun loginWithMagicToken_mapsInvalidMagicLink() = runBlocking {
        val fakeClient = object : HTTPClient {
            override suspend fun <T : Any> postJson(
                path: String,
                body: T
            ): Pair<String, java.net.HttpURLConnection> {
                val err = io.glovee.chatterbox.Core.Networking.NetworkError.RequestFailed(
                    400,
                    "{\"hint\":\"invalid_magic_link\"}"
                )
                throw err
            }
        }
        val context =
            androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val env = AppEnvironment(context)
        val repo = PostgrestAuthRepository(fakeClient, env, Gson())

        var thrown: Throwable? = null
        try {
            repo.loginWithMagicToken("any")
        } catch (t: Throwable) {
            thrown = t
        }
        assertTrue(thrown is AuthError.InvalidMagicLink)
    }

    @Test
    fun loginWithMagicToken_success_parsesTokens() = runBlocking {
        val responseJson = "{" +
                "\"access_token\":\"a\",\"refresh_token\":\"r\"" +
                "}"
        val fakeClient = object : HTTPClient {
            override suspend fun <T : Any> postJson(
                path: String,
                body: T
            ): Pair<String, java.net.HttpURLConnection> {
                val url = java.net.URL("https://example.com")
                val conn = (url.openConnection() as java.net.HttpURLConnection)
                return Pair(responseJson, conn)
            }
        }
        val context =
            androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val env = AppEnvironment(context)
        val repo = PostgrestAuthRepository(fakeClient, env, Gson())

        val tokens: AuthTokens = repo.loginWithMagicToken("token")
        assertEquals("a", tokens.accessToken)
        assertEquals("r", tokens.refreshToken)
    }
}


