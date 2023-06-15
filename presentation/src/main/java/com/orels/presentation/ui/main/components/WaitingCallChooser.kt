package com.orels.presentation.ui.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.orels.presentation.R

@Composable
fun WaitingCallChooser(
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    onDismissRequest: () -> Unit = {},
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnClickOutside = false,
        )
    ) {
        Column(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.inverseOnSurface)
                .shadow(1.dp)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
        ) {
            Text(
                text = stringResource(R.string.who_on_the_line),
                modifier = Modifier.padding(bottom = 24.dp),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineMedium
            )
            options.forEach { option ->
                Button(
                    onClick = { onOptionSelected(option) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.inverseOnSurface,
                    )
                ) {
                    Text(
                        text = option,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun WaitingCallChooserPreview() {
    WaitingCallChooser(
        options = listOf("Option 1", "Option 2"),
        onOptionSelected = {},
        onDismissRequest = {}
    )
}