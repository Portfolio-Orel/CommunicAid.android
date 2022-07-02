package com.orelzman.mymessages.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.orelzman.mymessages.R
import com.orelzman.mymessages.presentation.confirmation_screen.ConfirmationScreen
import com.orelzman.mymessages.presentation.destinations.MainScreenDestination
import com.orelzman.mymessages.presentation.login.components.Input
import com.orelzman.mymessages.presentation.login_button.LoginButton
import com.orelzman.mymessages.presentation.register_button.RegisterButton
import com.orelzman.mymessages.ui.theme.MyMessagesTheme
import com.orelzman.mymessages.util.extension.DefaultDestinationNavigator
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination(start = true)
fun LoginScreen(
    navigator: DestinationsNavigator,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state = viewModel.state

    if (state.isAuthorized) {
        navigator.navigate(MainScreenDestination)
    } else {
        if (state.showCodeConfirmation) {
            Column(
                modifier =
                Modifier
                    .zIndex(2f)
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f))
            ) {
                ConfirmationScreen(
                    username = state.username,
                    onDismiss = {
                        viewModel.hideRegistrationConfirmation()
                    }, onUserConfirmed = {
                        viewModel.onEvent(LoginEvents.UserRegisteredSuccessfully)
                    })
            }
        }
        if (state.isCheckingAuth) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(48.dp)
                        .width(48.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .zIndex(1f)
                    .fillMaxSize()
                    .padding(20.dp)
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.3f)),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier = Modifier.zIndex(1f),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    Input(
                        title = stringResource(R.string.user_name),
                        placeholder = stringResource(R.string.user_name),
                        initialText = "",
                        isPassword = false,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                stringResource(R.string.user_name)
                            )
                        },
                        onTextChange = { viewModel.onUsernameChange(it) }
                    )
                    Input(
                        title = stringResource(R.string.password),
                        placeholder = stringResource(R.string.password),
                        initialText = "",
                        isPassword = true,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                stringResource(R.string.password_icon)
                            )
                        },
                        onTextChange = { viewModel.onPasswordChange(it) })
                    if (state.isRegister) {
                        Input(
                            title = stringResource(R.string.email),
                            placeholder = stringResource(R.string.email),
                            initialText = "",
                            isPassword = false,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Lock,
                                    stringResource(R.string.email)
                                )
                            },
                            onTextChange = { viewModel.onEmailChange(it) })

                        RegisterButton(
                            username = state.username,
                            password = state.password,
                            email = state.email,
                            onRegisterComplete = { viewModel.onEvent(LoginEvents.UserRegisteredSuccessfully) })
                    } else {
                        LoginButton(
                            username = state.username,
                            password = state.password,
                            onLoginComplete = { isAuthorized, exception ->
                                viewModel.onEvent(
                                    LoginEvents.OnLoginCompleted(
                                        isAuthorized = isAuthorized,
                                        exception = exception
                                    )
                                )
                            })
                        Text(
                            stringResource(R.string.register),
                            modifier = Modifier
                                .clickable {
                                    viewModel.onRegisterClick()
                                },
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MyMessagesTheme {
        LoginScreen(
            navigator = DefaultDestinationNavigator()
        )
    }
}
