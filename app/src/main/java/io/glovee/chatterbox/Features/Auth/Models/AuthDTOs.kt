package io.glovee.chatterbox.Features.Auth.Models

data class RequestMagicLinkBody(val identifier: String)

data class LoginWithMagicTokenResponse(
    val access_token: String,
    val refresh_token: String,
)
