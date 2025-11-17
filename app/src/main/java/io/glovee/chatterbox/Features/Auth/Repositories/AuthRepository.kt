package io.glovee.chatterbox.Features.Auth.Repositories

import com.google.gson.Gson
import io.glovee.chatterbox.Core.Config.AppEnvironment
import io.glovee.chatterbox.Core.Networking.HTTPClient
import io.glovee.chatterbox.Core.Networking.NetworkError
import io.glovee.chatterbox.Core.Security.AuthTokens
import io.glovee.chatterbox.Features.Auth.Models.LoginWithMagicTokenResponse
import io.glovee.chatterbox.Features.Auth.Models.RequestMagicLinkBody

interface AuthRepository {
    suspend fun requestMagicLink(identifier: String)
    suspend fun loginWithMagicToken(token: String): AuthTokens
}

sealed class AuthError : Exception() {
    data object InvalidMagicLink : AuthError()
}

class PostgrestAuthRepository(
    private val client: HTTPClient,
    private val env: AppEnvironment,
    private val gson: Gson = Gson(),
) : AuthRepository {
    override suspend fun requestMagicLink(identifier: String) {
        client.postJson(env.requestMagicLinkPath, RequestMagicLinkBody(identifier))
    }

    override suspend fun loginWithMagicToken(token: String): AuthTokens {
        data class LoginBody(val token: String)
        return try {
            val (body, _) = client.postJson(env.loginWithMagicTokenPath, LoginBody(token))
            val decoded = gson.fromJson(body, LoginWithMagicTokenResponse::class.java)
            AuthTokens(decoded.access_token, decoded.refresh_token)
        } catch (e: Exception) {
            if (e is NetworkError.RequestFailed) {
                val b = e.body ?: ""
                if (b.contains("\"invalid_magic_link\"") || b.contains("\"hint\":\"invalid_magic_link\"")) {
                    throw AuthError.InvalidMagicLink
                }
            }
            throw e
        }
    }
}
