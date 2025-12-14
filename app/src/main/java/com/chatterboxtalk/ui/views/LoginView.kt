package com.chatterboxtalk.ui.views

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.chatterboxtalk.features.auth.viewmodel.AuthViewModel
import com.chatterboxtalk.R
import com.chatterboxtalk.ui.theme.AppColors
import com.chatterboxtalk.ui.theme.AppTypography
import com.chatterboxtalk.ui.theme.PillButton
import com.chatterboxtalk.ui.views.Strings

import androidx.compose.ui.draw.scale

/**
 * Android mirror of iOS `LoginView.swift`.
 * Matches iOS structure with logo, VStack, TextField, Button, error alert with URL support, and cooldown logic.
 */
@Composable
fun LoginView(viewModel: AuthViewModel) {
    val ctx = LocalContext.current
    val identifier by viewModel.identifier.collectAsState()
    val isRequesting by viewModel.isRequesting.collectAsState()
    val cooldownSecondsRemaining by viewModel.cooldownSecondsRemaining.collectAsState()
    val isShowingErrorAlert by viewModel.isShowingErrorAlert.collectAsState()
    val errorAlertTitle by viewModel.errorAlertTitle.collectAsState()
    val errorAlertMessage by viewModel.errorAlertMessage.collectAsState()
    val errorAlertLinkURL by viewModel.errorAlertLinkURL.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                // Theme token: keep aligned with iOS `AppColors.pageBackground`.
                AppColors.PageBackground
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), // iOS: .padding() default = 16pt
            verticalArrangement = Arrangement.spacedBy(24.dp), // iOS: VStack(spacing: 24)
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            // App Icon (iOS: Image("Logo").resizable().scaledToFit().frame(128x128).cornerRadius(20).shadow(radius: 6, y: 2))
            // We use the foreground vector drawable but scale it up to fill the 128dp frame better,
            // matching the iOS look where the logo is larger inside the rounded rect.
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(128.dp)
                    .drawBehind {
                        // iOS-style shadow: radius 6, offset y: 2, color: black @ 15%
                        drawIntoCanvas { canvas ->
                            val paint = android.graphics.Paint().apply {
                                color = AppColors.Beige.toArgb()
                                setShadowLayer(
                                    6.dp.toPx(), // radius (blur) - iOS: 6
                                    0f, // x offset - iOS: 0
                                    2.dp.toPx(), // y offset (downward) - iOS: 2
                                    AppColors.Shadow.toArgb() // shadow color
                                )
                            }
                            canvas.nativeCanvas.drawRoundRect(
                                0f, // left
                                0f, // top
                                size.width, // right
                                size.height, // bottom
                                20.dp.toPx(), // radiusX
                                20.dp.toPx(), // radiusY
                                paint
                            )
                        }
                    }
                    .clip(RoundedCornerShape(20.dp))
                    .background(AppColors.Beige)
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(1.4f) // Scale up the adaptive foreground to fill the box comfortably
                )
            }
            // Title (iOS: Text using displayMedium and textPrimary)
        Text(
            text = Strings.Login.title(ctx),
                style = AppTypography.displayMedium,
            color = AppColors.TextPrimary
        )

            // Form section (iOS: VStack(spacing: 24).padding(.bottom, Spacing.sm))
        Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp) // iOS: .padding(.bottom, Spacing.sm)
        ) {
                // FormTextField equivalent
                TextField(
                value = identifier,
                onValueChange = viewModel::updateIdentifier,
                placeholder = { Text(Strings.Login.identifierPlaceholder(ctx)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = Strings.A11y.identifierField(ctx)
                    },
                textStyle = AppTypography.body,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = AppColors.InputBackground, // iOS: AppColors.inputBackground
                        unfocusedContainerColor = AppColors.InputBackground,
                        disabledContainerColor = AppColors.InputBackground,
                    focusedTextColor = AppColors.TextPrimary,
                    unfocusedTextColor = AppColors.TextPrimary,
                        disabledTextColor = AppColors.TextPrimary,
                        cursorColor = AppColors.TextPrimary,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedPlaceholderColor = AppColors.TextPrimary.copy(alpha = 0.4f),
                        unfocusedPlaceholderColor = AppColors.TextPrimary.copy(alpha = 0.4f)
                ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done,
                        autoCorrectEnabled = false
                    )
                )

                // PillButton matching iOS (with opacity when disabled)
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .then(
                            if (isRequesting || cooldownSecondsRemaining > 0) {
                                Modifier.alpha(0.5f)
                            } else {
                                Modifier
                            }
                        )
                ) {
                    PillButton(
                text = Strings.Login.requestLink(ctx),
                onClick = { viewModel.requestMagicLink() },
                enabled = !isRequesting && cooldownSecondsRemaining == 0,
                        modifier = Modifier
            )
        }

                // iOS: .padding(.bottom, Spacing.sm) = 8pt
            }

            // Cooldown or hint message (iOS: footnote font, textSecondary color)
        if (cooldownSecondsRemaining > 0) {
            Text(
                text = Strings.Login.cooldownMessage(ctx, cooldownSecondsRemaining),
                style = AppTypography.footnote,
                    color = AppColors.TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text(
                text = Strings.Login.linkSentHint(ctx),
                style = AppTypography.footnote,
                    color = AppColors.TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.weight(1f))
        }
    }

    // Error alert matching iOS with support URL button
    if (isShowingErrorAlert) {
        val titleText = if (errorAlertTitle == "errors.sign_in_error_title") {
            Strings.Errors.signInErrorTitle(ctx)
        } else {
            Strings.Errors.signInErrorTitle(ctx)
        }
        
        // Display the actual error message (not the key)
        // If it's a key, look it up; otherwise display as-is
        val messageText = when (errorAlertMessage) {
            "errors.missing_identifier" -> Strings.Errors.missingIdentifier(ctx)
            "errors.invalid_magic_link" -> Strings.Errors.invalidMagicLink(ctx)
            "errors.request_failed" -> Strings.Errors.requestFailed(ctx)
            else -> errorAlertMessage // Display raw message (e.g., from server)
        }
        
        AlertDialog(
            onDismissRequest = { viewModel.dismissErrorAlert() },
            title = { Text(titleText) },
            text = { Text(messageText) },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissErrorAlert() }) {
                    Text(Strings.Common.ok(ctx))
                }
            },
            dismissButton = errorAlertLinkURL?.let { url ->
                {
                    TextButton(onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, url)
                        ctx.startActivity(intent)
                    }) {
                        Text(Strings.Login.openSupportPage(ctx))
                    }
                }
            }
        )
    }
}

