package com.chatterboxtalk.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.chatterboxtalk.core.security.TokenManager
import com.chatterboxtalk.ui.theme.AppColors
import com.chatterboxtalk.ui.theme.AppSpacing
import com.chatterboxtalk.ui.theme.AppTypography
import com.chatterboxtalk.ui.theme.DestructiveButton

/**
 * Android SettingsView using consistent design system.
 * Matches iOS SettingsView structure.
 */
@Composable
fun SettingsView(tokenManager: TokenManager) {
    val ctx = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.PageBackground)
            .padding(AppSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
    ) {
        Text(
            text = Strings.Settings.title(ctx),
            style = AppTypography.heading,
            color = AppColors.TextPrimary
        )
        
        Spacer(Modifier.weight(1f))
        
        DestructiveButton(
            text = Strings.Settings.logout(ctx),
            onClick = { tokenManager.clearTokens() },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = Strings.A11y.logout(ctx) }
        )
    }
}
