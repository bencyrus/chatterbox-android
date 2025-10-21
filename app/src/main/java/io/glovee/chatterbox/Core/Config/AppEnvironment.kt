package io.glovee.chatterbox.Core.Config

import android.content.Context
import android.net.Uri

class AppEnvironment(context: Context) {
    val baseUrl: String
    val requestMagicLinkPath: String = "/rpc/request_magic_link"
    val loginWithMagicTokenPath: String = "/rpc/login_with_magic_token"

    val universalLinkAllowedHosts: Set<String>
    val magicLinkPath: String

    val newAccessTokenHeaderOut: String = "X-New-Access-Token"
    val newRefreshTokenHeaderOut: String = "X-New-Refresh-Token"

    init {
        val res = context.resources
        val pkg = context.packageName
        val baseUrlStr = res.getIdentifier("api_base_url", "string", pkg)
        val hostsStrId = res.getIdentifier("universal_link_hosts", "string", pkg)
        val magicPathId = res.getIdentifier("magic_link_path", "string", pkg)

        require(baseUrlStr != 0) { "Missing string resource 'api_base_url'" }
        require(hostsStrId != 0) { "Missing string resource 'universal_link_hosts'" }
        require(magicPathId != 0) { "Missing string resource 'magic_link_path'" }

        baseUrl = res.getString(baseUrlStr)
        val hostsCsv = res.getString(hostsStrId)
        val parsedHosts = hostsCsv.split(',').map { it.trim().lowercase() }.filter { it.isNotEmpty() }
        require(parsedHosts.isNotEmpty()) { "universal_link_hosts must contain at least one hostname" }
        universalLinkAllowedHosts = parsedHosts.toSet()

        val magicRaw = res.getString(magicPathId).trim()
        require(magicRaw.isNotEmpty() && magicRaw.startsWith("/")) { "magic_link_path must start with '/'" }
        magicLinkPath = magicRaw
    }

    fun isValidMagicLink(uri: Uri): Boolean {
        val schemeOk = uri.scheme?.lowercase() == "https"
        val hostOk = uri.host?.lowercase()?.let { universalLinkAllowedHosts.contains(it) } == true
        val pathOk = uri.path?.lowercase() == magicLinkPath.lowercase()
        return schemeOk && hostOk && pathOk
    }
}
