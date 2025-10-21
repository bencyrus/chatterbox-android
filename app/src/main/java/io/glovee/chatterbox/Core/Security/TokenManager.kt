package io.glovee.chatterbox.Core.Security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

data class AuthTokens(val accessToken: String, val refreshToken: String)

interface TokenProvider {
    val accessToken: String?
    val refreshToken: String?
}

interface TokenSink {
    fun updateTokens(tokens: AuthTokens)
    fun clearTokens()
}

class TokenManager(context: Context): TokenProvider, TokenSink {
    private val prefs = run {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            "tokens",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    override val accessToken: String?
        get() = prefs.getString("access", null)

    override val refreshToken: String?
        get() = prefs.getString("refresh", null)

    override fun updateTokens(tokens: AuthTokens) {
        prefs.edit()
            .putString("access", tokens.accessToken)
            .putString("refresh", tokens.refreshToken)
            .apply()
    }

    override fun clearTokens() {
        prefs.edit().remove("access").remove("refresh").apply()
    }

    fun hasValidAccessToken(): Boolean = !accessToken.isNullOrBlank()

    fun addAccessTokenListener(callback: (Boolean) -> Unit): SharedPreferences.OnSharedPreferenceChangeListener {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "access") callback(hasValidAccessToken())
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        return listener
    }

    fun removeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
    }
}
