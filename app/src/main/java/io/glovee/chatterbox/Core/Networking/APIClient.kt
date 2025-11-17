package io.glovee.chatterbox.Core.Networking

import com.google.gson.Gson
import io.glovee.chatterbox.Core.Config.AppEnvironment
import io.glovee.chatterbox.Core.Security.AuthTokens
import io.glovee.chatterbox.Core.Security.TokenProvider
import io.glovee.chatterbox.Core.Security.TokenSink
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

sealed class NetworkError : Exception() {
    object InvalidUrl : NetworkError()
    data class RequestFailed(val code: Int, val body: String?) : NetworkError()
    object NoData : NetworkError()
    object DecodingFailed : NetworkError()
}

interface HTTPClient {
    suspend fun <T : Any> postJson(path: String, body: T): Pair<String, HttpURLConnection>
}

class APIClient(
    private val env: AppEnvironment,
    private val tokenProvider: TokenProvider,
    private val tokenSink: TokenSink?,
    private val gson: Gson = Gson(),
) : HTTPClient {

    override suspend fun <T : Any> postJson(
        path: String,
        body: T
    ): Pair<String, HttpURLConnection> {
        return withContext(Dispatchers.IO) {
            try {
                val url = try {
                    URL(env.baseUrl + path)
                } catch (e: Exception) {
                    throw NetworkError.InvalidUrl
                }
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    if (this is HttpsURLConnection) {
                        // Default platform TLS; certificate pinning could be added via SSLSocketFactory if required
                    }
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json")
                    tokenProvider.accessToken?.let {
                        setRequestProperty(
                            "Authorization",
                            "Bearer $it"
                        )
                    }
                    doOutput = true
                    connectTimeout = 10_000
                    readTimeout = 15_000
                }
                val json = gson.toJson(body)
                OutputStreamWriter(conn.outputStream).use { it.write(json) }

                val code = conn.responseCode
                val inputStream = if (code in 200..299) conn.inputStream else conn.errorStream
                val responseBody = inputStream?.use { stream ->
                    BufferedReader(InputStreamReader(stream)).readText()
                }
                captureRefreshedTokens(conn)
                if (code !in 200..299) {
                    throw NetworkError.RequestFailed(code, responseBody)
                }
                Pair(responseBody ?: "", conn)
            } catch (e: NetworkError) {
                throw e
            } catch (e: Exception) {
                throw e
            }
        }
    }

    private fun captureRefreshedTokens(conn: HttpURLConnection) {
        val access = conn.getHeaderField(env.newAccessTokenHeaderOut)
        val refresh = conn.getHeaderField(env.newRefreshTokenHeaderOut)
        if (!access.isNullOrBlank() && !refresh.isNullOrBlank()) {
            tokenSink?.updateTokens(AuthTokens(access, refresh))
        }
    }
}
