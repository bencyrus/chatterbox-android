package com.chatterboxtalk.Core.Networking

import com.chatterboxtalk.Features.Auth.Models.LoginWithMagicTokenResponse
import com.chatterboxtalk.Features.Auth.Models.RequestMagicLinkBody

/**
 * Android mirror of the iOS `Endpoints.swift` structure.
 * Centralizes endpoint definitions and paths for consistency with iOS.
 */

// MARK: - Common

data class NoResponseBody(val unused: String? = null)

// MARK: - Auth

object AuthEndpoints {
    data class RequestMagicLink(
        val path: String = "/rpc/request_magic_link",
        val requiresAuth: Boolean = false
    )

    data class LoginWithMagicToken(
        val path: String = "/rpc/login_with_magic_token",
        val requiresAuth: Boolean = false
    ) {
        data class Body(val token: String)
    }

    data class ReviewerLogin(
        val path: String = "/rpc/reviewer_login",
        val requiresAuth: Boolean = false
    )
}

