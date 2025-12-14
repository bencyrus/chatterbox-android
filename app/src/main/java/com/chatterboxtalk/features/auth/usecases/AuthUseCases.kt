package com.chatterboxtalk.features.auth.usecases

import com.chatterboxtalk.core.observability.AnalyticsEvent
import com.chatterboxtalk.core.observability.AnalyticsRecording
import com.chatterboxtalk.core.security.TokenSink
import com.chatterboxtalk.features.auth.repositories.AuthRepository

/**
 * Android mirror of iOS `AuthUseCases.swift`.
 * Use cases orchestrate domain logic and analytics recording.
 */

class LogoutUseCase(
    private val tokenSink: TokenSink
) {
    fun execute() {
        tokenSink.clearTokens()
    }
}

class RequestMagicLinkUseCase(
    private val repository: AuthRepository,
    private val tokenSink: TokenSink,
    private val analytics: AnalyticsRecording? = null
) {
    suspend fun execute(identifier: String): Boolean {
        val tokens = repository.requestMagicLink(identifier)
        return if (tokens != null) {
            // Reviewer login: tokens returned immediately
            tokenSink.updateTokens(tokens)
            analytics?.record(
                AnalyticsEvent(
                    name = "auth.reviewer_login_success",
                    properties = emptyMap(),
                    context = emptyMap()
                )
            )
            true
        } else {
            // Normal magic link flow: email sent
        analytics?.record(
            AnalyticsEvent(
                name = "auth.magic_link_requested",
                properties = emptyMap(),
                context = emptyMap()
            )
        )
            false
        }
    }
}

class LoginWithMagicTokenUseCase(
    private val repository: AuthRepository,
    private val tokenSink: TokenSink,
    private val analytics: AnalyticsRecording? = null
) {
    suspend fun execute(token: String) {
        val tokens = repository.loginWithMagicToken(token)
        tokenSink.updateTokens(tokens)
        analytics?.record(
            AnalyticsEvent(
                name = "auth.login_success",
                properties = emptyMap(),
                context = emptyMap()
            )
        )
    }
}
