package io.glovee.chatterbox.Features.Auth.UseCases

import io.glovee.chatterbox.Core.Security.TokenSink
import io.glovee.chatterbox.Features.Auth.Repositories.AuthRepository

class LogoutUseCase(private val tokenSink: TokenSink) {
    fun execute() {
        tokenSink.clearTokens()
    }
}

class RequestMagicLinkUseCase(private val repository: AuthRepository) {
    suspend fun execute(identifier: String) {
        repository.requestMagicLink(identifier)
    }
}

class LoginWithMagicTokenUseCase(
    private val repository: AuthRepository,
    private val tokenSink: TokenSink
) {
    suspend fun execute(token: String) {
        val tokens = repository.loginWithMagicToken(token)
        tokenSink.updateTokens(tokens)
    }
}
