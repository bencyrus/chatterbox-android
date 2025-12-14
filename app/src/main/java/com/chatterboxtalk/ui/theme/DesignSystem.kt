package com.chatterboxtalk.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.chatterboxtalk.ui.theme.AppColors

/**
 * Android mirror of iOS `DesignSystem.swift`.
 * Provides button styles and common components matching iOS design.
 */

// MARK: - Button Styles

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AppColors.TextPrimary,
            contentColor = AppColors.TextContrast,
            disabledContainerColor = AppColors.TextPrimary.copy(alpha = 0.5f),
            disabledContentColor = AppColors.TextContrast.copy(alpha = 0.5f)
        ),
        contentPadding = PaddingValues(
            vertical = AppSpacing.sm,
            horizontal = AppSpacing.md
        )
    ) {
        Text(
            text = text,
            style = AppTypography.body.copy(fontWeight = FontWeight.Medium)
        )
    }
}

/** Rounded primary button for prominent CTAs (e.g. sign-in). */
@Composable
fun PillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(50), // Capsule shape
        colors = ButtonDefaults.buttonColors(
            containerColor = AppColors.TextPrimary,
            contentColor = AppColors.TextContrast,
            disabledContainerColor = AppColors.TextPrimary.copy(alpha = 0.5f),
            disabledContentColor = AppColors.TextContrast.copy(alpha = 0.5f)
        ),
        contentPadding = PaddingValues(
            vertical = AppSpacing.md,
            horizontal = AppSpacing.xl
        )
    ) {
        Text(
            text = text,
            style = AppTypography.body.copy(fontWeight = FontWeight.Medium)
        )
    }
}

@Composable
fun DestructiveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.Red,
            disabledContentColor = Color.Red.copy(alpha = 0.5f)
        ),
        border = BorderStroke(1.dp, Color.Red),
        contentPadding = PaddingValues(
            vertical = AppSpacing.sm,
            horizontal = AppSpacing.md
        )
    ) {
        Text(
            text = text,
            style = AppTypography.body.copy(fontWeight = FontWeight.Medium)
        )
    }
}

// MARK: - Common Components

@Composable
fun PageHeader(
    title: String,
    modifier: Modifier = Modifier,
    actions: @Composable () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = AppTypography.heading,
            color = AppColors.TextPrimary,
            modifier = Modifier.weight(1f)
        )
        
        actions()
    }
}

