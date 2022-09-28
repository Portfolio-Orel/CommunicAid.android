package com.orelzman.mymessages.presentation.login.components.forgot_password

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.orelzman.mymessages.R
import com.orelzman.mymessages.presentation.login.components.Input
import com.orelzman.mymessages.presentation.main.components.ActionButton

/**
 * @author Orel Zilberman
 * 28/09/2022
 */

@Composable
fun ForgotPasswordComponent(
    modifier: Modifier = Modifier,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val state = viewModel.state
    var shouldShowDialog by remember { mutableStateOf(false) }
    Column(modifier = Modifier.padding(8.dp)) {
        Box(modifier = modifier) {
            Text(
                modifier = Modifier.clickable {
                    shouldShowDialog = true
                    viewModel.forgotPassword()
                },
                text = stringResource(R.string.did_forget_password),
                style = MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.Underline),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f)
            )
            if (shouldShowDialog) {
                Dialog(onDismissRequest = { }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopStart)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "סגור חלון איפוס סיסמא",
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    shouldShowDialog = false
                                },
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    when (state.event) {
                        ForgotPasswordEvent.Default -> {}
                        ForgotPasswordEvent.InsertUsername, ForgotPasswordEvent.InsertCodeAndPassword -> {
                            Column(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.9f))
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(
                                    12.dp,
                                    Alignment.CenterVertically
                                )
                            ) {
                                if (state.event == ForgotPasswordEvent.InsertUsername) {
                                    InsertUsername(
                                        isLoading = state.isLoading,
                                        onClick = viewModel::insertUsername
                                    )
                                } else {
                                    InsertCodeAndPasswords(
                                        isLoading = state.isLoading,
                                        onClick = viewModel::insertCodeAndPasswords
                                    )
                                }
                            }
                        }
                        ForgotPasswordEvent.PasswordResetSuccessfully -> {
                            shouldShowDialog = false
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InsertUsername(
    isLoading: Boolean,
    onClick: (String) -> Unit,
    errorFields: List<ForgotPasswordFields> = emptyList()
) {
    var username = ""

    Text(
        stringResource(R.string.insert_username),
        modifier = Modifier.padding(16.dp),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground,
    )
    Input(
        modifier = Modifier.padding(horizontal = 16.dp),
        title = stringResource(R.string.username),
        placeholder = stringResource(R.string.confirmation_code),

        onTextChange = {
            username = it
        })
    ActionButton(
        text = stringResource(R.string.reset_password),
        onClick = { onClick(username) },
        isLoading = isLoading
    )
}

@Composable
fun InsertCodeAndPasswords(
    isLoading: Boolean,
    onClick: (String, String, String) -> Unit,
    errorFields: List<ForgotPasswordFields> = emptyList()
) {
    var code = ""
    var password = ""
    var confirmPassword = ""

    Input(
        modifier = Modifier.padding(horizontal = 16.dp),
        stringResource(R.string.insert_email_code),
        placeholder = stringResource(R.string.confirmation_code),
        onTextChange = {

        })
    ActionButton(
        text = stringResource(R.string.check),
        onClick = { onClick(code, password, confirmPassword) },
        isLoading = isLoading
    )
}

@Composable
fun ErrorText(@StringRes res: Int?) {
    if (res != null) {
        Text(
            text = stringResource(id = res),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
    }
}