package com.orels.presentation.ui.components.confirmation_pop_up

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.orels.presentation.R
import com.orels.presentation.theme.MyMessagesTheme
import com.orels.presentation.ui.components.Input

@ExperimentalMaterial3Api
@Composable
fun ConfirmationScreen(
    username: String,
    password: String,
    onDismiss: () -> Unit,
    onUserConfirmed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ConfirmationViewModel = hiltViewModel()
) {
    val state = viewModel.state
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .padding(16.dp)
                .heightIn(min = 220.dp, max = 320.dp)
                .width(320.dp)
                .zIndex(2f),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
        ) {
          Column(modifier = Modifier.padding(bottom = 24.dp)) {
            if (!state.isLoading) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 8.dp, top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.insert_email_code),
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 12.dp),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = { onDismiss() }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close window",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                if (state.error != null) {
                    Text(
                        stringResource(state.error),
                        modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Input(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    title = "",
                    placeholder = stringResource(R.string.confirmation_code),
                    initialText = "",
                    isPassword = false,
                    onTextChange = {
                        viewModel.onCodeChange(
                            value = it,
                            username = username,
                            password = password,
                            onUserConfirmed = onUserConfirmed
                        )
                    })
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .height(40.dp)
                            .width(40.dp),
                        strokeWidth = 3.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.confirming_code),
                        modifier = Modifier.padding(horizontal = 24.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
          }
        }
    }
}

@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MyMessagesTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            ConfirmationScreen(
                username = "",
                password = "",
                onDismiss = {},
                onUserConfirmed = {},
            )
        }
    }
}
