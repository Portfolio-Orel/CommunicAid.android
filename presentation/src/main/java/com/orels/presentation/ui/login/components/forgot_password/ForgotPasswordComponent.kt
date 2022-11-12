package com.orels.presentation.ui.login.components.forgot_password

import androidx.annotation.StringRes
import androidx.compose.animation.*
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.orels.presentation.R
import com.orels.presentation.ui.login.components.Input
import com.orels.presentation.ui.main.components.ActionButton

/**
 * @author Orel Zilberman
 * 28/09/2022
 */

@OptIn(ExperimentalAnimationApi::class)
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
                Dialog(
                    onDismissRequest = { shouldShowDialog = false },
                    properties = DialogProperties(
                        dismissOnBackPress = true,
                        dismissOnClickOutside = false
                    )
                ) {
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
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .heightIn(min = 480.dp, max = 1000.dp)
                                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.9f))
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                AnimatedContent(targetState = state.event) {
                                    if (state.event == ForgotPasswordEvent.InsertUsername) {
                                        InsertUsername(
                                            modifier = Modifier.animateEnterExit(
                                                enter = fadeIn(),
                                                exit = ExitTransition.None
                                            ),
                                            isLoading = state.isLoading,
                                            onClick = viewModel::insertUsername,
                                            errorFields = state.errorFields,
                                            error = state.error
                                        )
                                    } else {
                                        InsertCodeAndPasswords(
                                            modifier = Modifier.animateEnterExit(
                                                enter = fadeIn(),
                                                exit = ExitTransition.None
                                            ),
                                            isLoading = state.isLoading,
                                            onClick = viewModel::insertCodeAndPasswords,
                                            errorFields = state.errorFields,
                                            error = state.error
                                        )
                                    }
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InsertUsername(
    isLoading: Boolean,
    onClick: (String) -> Unit,
    errorFields: List<ForgotPasswordFields>,
    modifier: Modifier = Modifier,
    @StringRes error: Int? = null
) {
    var username by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier.wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            12.dp,
            Alignment.CenterVertically
        )
    ) {
        Text(
            stringResource(R.string.insert_username),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        if (error != null) {
            ErrorText(error = error)
        }

        Input(
            modifier = Modifier.padding(horizontal = 16.dp),
            title = stringResource(R.string.username),
            placeholder = stringResource(R.string.username),
            isError = errorFields.contains(ForgotPasswordFields.Username),
            shouldFocus = true,
            onTextChange = {
                username = it
            })
        ActionButton(
            text = stringResource(R.string.reset_password),
            onClick = {
                keyboardController?.hide()
                onClick(username)
            },
            isLoading = isLoading
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InsertCodeAndPasswords(
    isLoading: Boolean,
    onClick: (String, String, String) -> Unit,
    errorFields: List<ForgotPasswordFields>,
    modifier: Modifier = Modifier,
    @StringRes error: Int? = null
) {
    var code by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier.height(500.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            12.dp,
            Alignment.CenterVertically
        )
    ) {

        Text(
            stringResource(R.string.insert_email_code),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        if (error != null) {
            ErrorText(error = error)
        }

        Input(
            modifier = Modifier.padding(horizontal = 16.dp),
            title = stringResource(R.string.confirmation_code),
            placeholder = stringResource(R.string.confirmation_code),
            isError = errorFields.contains(ForgotPasswordFields.Code),
            shouldFocus = true,
            onTextChange = {
                code = it
            })
        Input(
            modifier = Modifier.padding(horizontal = 16.dp),
            title = stringResource(R.string.password),
            isPassword = true,
            placeholder = stringResource(R.string.password),
            isError = errorFields.contains(ForgotPasswordFields.Password),
            onTextChange = {
                password = it
            })
        Input(
            modifier = Modifier.padding(horizontal = 16.dp),
            title = stringResource(R.string.confirm_password),
            isPassword = true,
            placeholder = stringResource(R.string.confirm_password),
            isError = errorFields.contains(ForgotPasswordFields.ConfirmPassword),
            onTextChange = {
                confirmPassword = it
            })
        Spacer(Modifier.weight(1f))
        ActionButton(
            text = stringResource(R.string.change_password),
            onClick = {
                keyboardController?.hide()
                onClick(code, password, confirmPassword)
            },
            isLoading = isLoading
        )
    }
}

@Composable
fun ErrorText(@StringRes error: Int?) {
    AnimatedVisibility(visible = error != null) {
        error?.let {
            Text(
                text = stringResource(id = it),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}