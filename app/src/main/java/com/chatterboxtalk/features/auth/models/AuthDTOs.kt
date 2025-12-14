package com.chatterboxtalk.features.auth.models

import com.google.gson.annotations.SerializedName

data class RequestMagicLinkBody(
    val identifier: String
)

data class LoginWithMagicTokenResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String
)
