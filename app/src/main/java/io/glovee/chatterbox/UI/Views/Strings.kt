package io.glovee.chatterbox.UI.Views

import android.content.Context
import io.glovee.chatterbox.R

object Strings {
    object Tabs {
        fun home(ctx: Context) = ctx.getString(R.string.tabs_home)
        fun settings(ctx: Context) = ctx.getString(R.string.tabs_settings)
    }

    object Login {
        fun title(ctx: Context) = ctx.getString(R.string.login_title)
        fun identifierPlaceholder(ctx: Context) =
            ctx.getString(R.string.login_identifier_placeholder)

        fun requestLink(ctx: Context) = ctx.getString(R.string.login_request_link)
        fun linkSentHint(ctx: Context) = ctx.getString(R.string.login_link_sent_hint)
        fun cooldownMessage(ctx: Context, seconds: Int) =
            ctx.getString(R.string.login_cooldown_message, seconds)
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
        fun missingIdentifier(ctx: Context) = ctx.getString(R.string.errors_missing_identifier)
        fun requestFailed(ctx: Context) = ctx.getString(R.string.errors_request_failed)
        fun invalidMagicLink(ctx: Context) = ctx.getString(R.string.errors_invalid_magic_link)
    }

    object A11y {
        fun identifierField(ctx: Context) = ctx.getString(R.string.a11y_identifier_field)
        fun error(ctx: Context) = ctx.getString(R.string.a11y_error)
        fun logout(ctx: Context) = ctx.getString(R.string.a11y_logout)
    }
}
