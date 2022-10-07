package com.orels.presentation.ui.components.confirmation_pop_up

import androidx.compose.foundation.clickable
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
import com.orels.presentation.ui.login.components.Input

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
        Box(modifier = Modifier.zIndex(1f)) {
            MaterialTheme.colorScheme.primary
        }
        Card(
            modifier = Modifier
                .padding(16.dp)
                .heightIn(min = 220.dp, max = 300.dp)
                .width(300.dp)
                .zIndex(2f),
        ) {
            if (!state.isLoading) {
                Icon(
                    modifier = Modifier
                        .clickable {
                            onDismiss()
                        }
                        .height(60.dp)
                        .width(60.dp)
                        .padding(16.dp),
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close window",
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    stringResource(R.string.insert_email_code),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (state.error != null) {
                    Text(
                        stringResource(state.error),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Input(
                    modifier = Modifier.padding(horizontal = 16.dp),
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
                            .height(36.dp)
                            .width(36.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = stringResource(R.string.confirming_code),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
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
