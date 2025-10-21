package io.glovee.chatterbox.Features.Auth.Repositories

import com.google.gson.Gson
import io.glovee.chatterbox.Core.Config.AppEnvironment
import io.glovee.chatterbox.Core.Networking.HTTPClient
import io.glovee.chatterbox.Core.Security.AuthTokens
import io.glovee.chatterbox.Features.Auth.Models.LoginWithMagicTokenResponse
import io.glovee.chatterbox.Features.Auth.Models.RequestMagicLinkBody

interface AuthRepository {
    suspend fun requestMagicLink(identifier: String)
    suspend fun loginWithMagicToken(token: String): AuthTokens
}

class PostgrestAuthRepository(
    private val client: HTTPClient,
    private val env: AppEnvironment,
    private val gson: Gson = Gson(),
): AuthRepository {
    override suspend fun requestMagicLink(identifier: String) {
        client.postJson(env.requestMagicLinkPath, RequestMagicLinkBody(identifier))
    }

    override suspend fun loginWithMagicToken(token: String): AuthTokens {
        data class LoginBody(val token: String)
        val (body, _) = client.postJson(env.loginWithMagicTokenPath, LoginBody(token))
        val decoded = gson.fromJson(body, LoginWithMagicTokenResponse::class.java)
        return AuthTokens(decoded.access_token, decoded.refresh_token)
    }
}
