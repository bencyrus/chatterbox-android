package com.chatterboxtalk.Features.Auth.Repositories

import com.google.gson.Gson
import com.chatterboxtalk.Core.Config.AppEnvironment
import com.chatterboxtalk.Core.Networking.AuthEndpoints
import com.chatterboxtalk.Core.Networking.HTTPClient
import com.chatterboxtalk.Core.Networking.NetworkError
import com.chatterboxtalk.Core.Security.AuthTokens
import com.chatterboxtalk.Features.Auth.Models.LoginWithMagicTokenResponse
import com.chatterboxtalk.Features.Auth.Models.RequestMagicLinkBody

/**
 * Android mirror of iOS `AuthRepository.swift`.
 * Handles auth endpoints with identical error handling logic.
 */

interface AuthRepository {
    suspend fun requestMagicLink(identifier: String): AuthTokens?
    suspend fun loginWithMagicToken(token: String): AuthTokens
}

sealed class AuthError : Exception() {
    data object InvalidMagicLink : AuthError()
    data class AccountDeleted(val message: String) : AuthError()
}

class PostgrestAuthRepository(
    private val client: HTTPClient,
    private val env: AppEnvironment,
    private val gson: Gson = Gson(),
) : AuthRepository {
    
    override suspend fun requestMagicLink(identifier: String): AuthTokens? {
        // Check if this is the reviewer account
        val reviewerEmail = env.reviewerEmail
        if (reviewerEmail != null && 
            identifier.trim().lowercase() == reviewerEmail.lowercase()) {
            // Use the reviewer login endpoint that returns tokens immediately
            val endpoint = AuthEndpoints.ReviewerLogin()
            val body = RequestMagicLinkBody(identifier)
            val (responseBody, _) = client.postJson(endpoint.path, body)
            val response = gson.fromJson(responseBody, LoginWithMagicTokenResponse::class.java)
            return AuthTokens(response.accessToken, response.refreshToken)
        }
        
        // Normal magic link flow
        val endpoint = AuthEndpoints.RequestMagicLink()
        val body = RequestMagicLinkBody(identifier)
        try {
            client.postJson(endpoint.path, body)
            return null
        } catch (e: Exception) {
            if (e is NetworkError.RequestFailed) {
                val errorBody = e.body ?: ""
                if (errorBody.contains("\"hint\":\"account_deleted\"") || 
                    errorBody.contains("account_deleted")) {
                    // Extract user-facing message from PostgREST error body
                    val message = extractPostgrestUserMessage(errorBody)
                        ?: "Your account was deleted. Visit chatterboxtalk.com to contact support to reactivate your account."
                    throw AuthError.AccountDeleted(message)
                }
            }
            throw e
        }
    }

    override suspend fun loginWithMagicToken(token: String): AuthTokens {
        val endpoint = AuthEndpoints.LoginWithMagicToken()
        val body = AuthEndpoints.LoginWithMagicToken.Body(token)
        return try {
            val (responseBody, _) = client.postJson(endpoint.path, body)
            val response = gson.fromJson(responseBody, LoginWithMagicTokenResponse::class.java)
            AuthTokens(response.accessToken, response.refreshToken)
        } catch (e: Exception) {
            if (e is NetworkError.RequestFailed) {
                val errorBody = e.body ?: ""
                if (errorBody.contains("\"hint\":\"invalid_magic_link\"") || 
                    errorBody.contains("invalid_magic_link")) {
                    throw AuthError.InvalidMagicLink
                }
                if (errorBody.contains("\"hint\":\"account_deleted\"") || 
                    errorBody.contains("account_deleted")) {
                    val message = extractPostgrestUserMessage(errorBody)
                        ?: "Your account was deleted. Visit chatterboxtalk.com to contact support to reactivate your account."
                    throw AuthError.AccountDeleted(message)
                }
            }
            throw e
        }
    }

    /**
     * Best-effort extraction of a user-facing message from a PostgREST-style error body.
     * Prefers the "details" field when present (since it contains the full copy),
     * otherwise falls back to "message".
     */
    private fun extractPostgrestUserMessage(responseBody: String): String? {
        extractPostgrestField("details", responseBody)?.let { return it }
        return extractPostgrestField("message", responseBody)
    }

    /**
     * Lightweight extraction of a JSON string field (e.g. "details":"...") from the body.
     */
    private fun extractPostgrestField(field: String, responseBody: String): String? {
        val fieldPattern = "\"$field\""
        val fieldIndex = responseBody.indexOf(fieldPattern)
        if (fieldIndex == -1) return null

        val afterField = responseBody.substring(fieldIndex + fieldPattern.length)
        val colonIndex = afterField.indexOf(':')
        if (colonIndex == -1) return null

        val afterColon = afterField.substring(colonIndex + 1)
        val firstQuoteIndex = afterColon.indexOf('"')
        if (firstQuoteIndex == -1) return null

        val remainder = afterColon.substring(firstQuoteIndex + 1)
        val secondQuoteIndex = remainder.indexOf('"')
        if (secondQuoteIndex == -1) return null

        return remainder.substring(0, secondQuoteIndex)
    }
}
