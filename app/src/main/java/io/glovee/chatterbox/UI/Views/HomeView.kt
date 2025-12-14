package com.chatterboxtalk.UI.Views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import com.chatterboxtalk.Core.Security.TokenManager
import com.chatterboxtalk.UI.Theme.AppSpacing
import com.chatterboxtalk.UI.Theme.AppTypography

/**
 * Android HomeView using consistent design system.
 */
@Composable
fun HomeView(tokenManager: TokenManager) {
    val ctx = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Sand)
            .padding(AppSpacing.md)
    ) {
        Text(
            text = Strings.Home.latestJWT(ctx), 
            style = AppTypography.heading,
            color = AppColors.TextPrimary
        )
        Spacer(Modifier.height(AppSpacing.sm))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = tokenManager.accessToken ?: Strings.Home.noToken(ctx),
                style = AppTypography.caption.copy(fontFamily = FontFamily.Monospace),
                color = AppColors.TextPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.Beige)
                    .padding(AppSpacing.md)
            )
        }
    }
}
