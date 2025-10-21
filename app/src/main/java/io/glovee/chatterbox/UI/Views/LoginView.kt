package io.glovee.chatterbox.UI.Views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.glovee.chatterbox.Features.Auth.ViewModel.AuthViewModel

@Composable
fun LoginView(viewModel: AuthViewModel) {
    val ctx = LocalContext.current
    val identifier by viewModel.identifier.collectAsState()
    val isRequesting by viewModel.isRequesting.collectAsState()
    val errorMessageKey by viewModel.errorMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.95f))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(Strings.Login.title(ctx), style = MaterialTheme.typography.headlineSmall)
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = identifier,
                onValueChange = viewModel::updateIdentifier,
                label = { Text(Strings.Login.identifierPlaceholder(ctx)) },
                modifier = Modifier.semantics { contentDescription = Strings.A11y.identifierField(ctx) }
            )
            Button(onClick = { viewModel.requestMagicLink() }, enabled = !isRequesting) {
                Text(Strings.Login.requestLink(ctx))
            }
        }
        if (errorMessageKey.isNotEmpty()) {
            val msg = when (errorMessageKey) {
                "errors.missing_identifier" -> Strings.Errors.missingIdentifier(ctx)
                else -> Strings.Errors.requestFailed(ctx)
            }
            Text(
                text = msg,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray,
                modifier = Modifier.semantics { contentDescription = Strings.A11y.error(ctx) }
            )
        }
        Text(
            Strings.Login.linkSentHint(ctx),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Spacer(Modifier.weight(1f))
    }
}
