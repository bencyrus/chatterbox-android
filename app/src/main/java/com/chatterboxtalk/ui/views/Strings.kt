package com.chatterboxtalk.ui.views

import android.content.Context
import com.chatterboxtalk.R

object Strings {
    object Tabs {
        fun history(ctx: Context) = ctx.getString(R.string.tabs_history)
        fun subjects(ctx: Context) = ctx.getString(R.string.tabs_subjects)
        fun home(ctx: Context) = ctx.getString(R.string.tabs_home)
        fun settings(ctx: Context) = ctx.getString(R.string.tabs_settings)
        fun debug(ctx: Context) = ctx.getString(R.string.tabs_debug)
    }

    object Login {
        fun title(ctx: Context) = ctx.getString(R.string.login_title)
        fun identifierPlaceholder(ctx: Context) =
            ctx.getString(R.string.login_identifier_placeholder)

        fun requestLink(ctx: Context) = ctx.getString(R.string.login_request_link)
        fun linkSentHint(ctx: Context) = ctx.getString(R.string.login_link_sent_hint)
        fun cooldownMessage(ctx: Context, seconds: Int) =
            ctx.getString(R.string.login_cooldown_message, seconds)
        fun openSupportPage(ctx: Context) = ctx.getString(R.string.login_open_support_page)
    }

    object Home {
        fun title(ctx: Context) = ctx.getString(R.string.home_title)
        fun latestJWT(ctx: Context) = ctx.getString(R.string.home_latest_jwt)
        fun noToken(ctx: Context) = ctx.getString(R.string.home_no_token)
    }

    object Settings {
        fun title(ctx: Context) = ctx.getString(R.string.settings_title)
        fun logout(ctx: Context) = ctx.getString(R.string.settings_logout)
    }

    object Errors {
        fun signInErrorTitle(ctx: Context) = ctx.getString(R.string.errors_sign_in_error_title)
        fun missingIdentifier(ctx: Context) = ctx.getString(R.string.errors_missing_identifier)
        fun requestFailed(ctx: Context) = ctx.getString(R.string.errors_request_failed)
        fun invalidMagicLink(ctx: Context) = ctx.getString(R.string.errors_invalid_magic_link)
    }

    object A11y {
        fun identifierField(ctx: Context) = ctx.getString(R.string.a11y_identifier_field)
        fun error(ctx: Context) = ctx.getString(R.string.a11y_error)
        fun logout(ctx: Context) = ctx.getString(R.string.a11y_logout)
    }

    object Common {
        fun ok(ctx: Context) = ctx.getString(R.string.common_ok)
        fun cancel(ctx: Context) = ctx.getString(R.string.common_cancel)
    }
}
