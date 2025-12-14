package com.chatterboxtalk.UI.Views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.chatterboxtalk.Features.Auth.ViewModel.AuthViewModel
import com.chatterboxtalk.UI.Theme.AppSpacing
import com.chatterboxtalk.UI.Theme.AppTypography
import com.chatterboxtalk.UI.Theme.PrimaryButton

/**
 * Android mirror of iOS `LoginView.swift`.
 * Matches iOS structure with VStack, TextField, Button, error alert, and cooldown logic.
 */
@Composable
fun LoginView(viewModel: AuthViewModel) {
    val ctx = LocalContext.current
    val identifier by viewModel.identifier.collectAsState()
    val isRequesting by viewModel.isRequesting.collectAsState()
    val errorMessageKey by viewModel.errorMessage.collectAsState()
    val cooldownSecondsRemaining by viewModel.cooldownSecondsRemaining.collectAsState()
    val isShowingErrorAlert by viewModel.isShowingErrorAlert.collectAsState()
    val errorAlertTitle by viewModel.errorAlertTitle.collectAsState()
    val errorAlertMessage by viewModel.errorAlertMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Sand)
            .padding(AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
    ) {
        Text(
            text = Strings.Login.title(ctx),
            style = AppTypography.title,
            color = AppColors.TextPrimary
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = identifier,
                onValueChange = viewModel::updateIdentifier,
                placeholder = { Text(Strings.Login.identifierPlaceholder(ctx)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = Strings.A11y.identifierField(ctx)
                    },
                textStyle = AppTypography.body,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = AppColors.Beige,
                    unfocusedContainerColor = AppColors.Beige,
                    focusedTextColor = AppColors.TextPrimary,
                    unfocusedTextColor = AppColors.TextPrimary,
                    cursorColor = AppColors.TextPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            PrimaryButton(
                text = Strings.Login.requestLink(ctx),
                onClick = { viewModel.requestMagicLink() },
                enabled = !isRequesting && cooldownSecondsRemaining == 0,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (cooldownSecondsRemaining > 0) {
            Text(
                text = Strings.Login.cooldownMessage(ctx, cooldownSecondsRemaining),
                style = AppTypography.footnote,
                color = AppColors.TextPrimary.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text(
                text = Strings.Login.linkSentHint(ctx),
                style = AppTypography.footnote,
                color = AppColors.TextPrimary.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.weight(1f))
    }

    // Error alert matching iOS alert behavior
    if (isShowingErrorAlert) {
        val titleText = when (errorAlertTitle) {
            "errors.sign_in_error_title" -> Strings.Errors.signInErrorTitle(ctx)
            else -> Strings.Errors.signInErrorTitle(ctx)
        }
        val messageText = when (errorAlertMessage) {
            "errors.missing_identifier" -> Strings.Errors.missingIdentifier(ctx)
            "errors.invalid_magic_link" -> Strings.Errors.invalidMagicLink(ctx)
            "errors.request_failed" -> Strings.Errors.requestFailed(ctx)
            else -> Strings.Errors.requestFailed(ctx)
        }
        
        AlertDialog(
            onDismissRequest = { viewModel.dismissErrorAlert() },
            title = { Text(titleText) },
            text = { Text(messageText) },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissErrorAlert() }) {
                    Text("OK")
                }
            }
        )
    }
}

