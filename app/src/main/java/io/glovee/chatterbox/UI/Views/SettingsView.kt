package io.glovee.chatterbox.UI.Views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.glovee.chatterbox.Core.Security.TokenManager

@Composable
fun SettingsView(tokenManager: TokenManager) {
    val ctx = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.95f))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(Modifier.weight(1f))
        Button(
            onClick = { tokenManager.clearTokens() },
            modifier = Modifier.semantics { contentDescription = Strings.A11y.logout(ctx) }) {
            Text(
                Strings.Settings.logout(ctx),
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(Modifier.weight(1f))
    }
}
