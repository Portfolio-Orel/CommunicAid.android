package com.orelzman.mymessages.presentation.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.orelzman.mymessages.R
import com.orelzman.mymessages.presentation.destinations.MainScreenDestination
import com.orelzman.mymessages.presentation.login.components.Input
import com.orelzman.mymessages.presentation.login.register_button.RegisterButton
import com.orelzman.mymessages.presentation.login_button.LoginButton
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
    }
    Box {
        if (state.showCodeConfirmation) {
            ConfirmPopup(onConfirmationCodeEntered = {
                viewModel.onEvent(
                    LoginEvents.ConfirmSignup(
                        it
                    )
                )
            })
        }
        Column(modifier = Modifier.zIndex(1f)) {
            Input(
                modifier = Modifier.padding(20.dp),
                title = stringResource(R.string.user_name),
                placeholder = stringResource(R.string.user_name),
                initialText = "user123",
                isPassword = false,
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Person, stringResource(R.string.user_name))
                },
                onTextChange = { viewModel.onUsernameChange(it) }
            )
            Input(
                modifier = Modifier.padding(20.dp),
                title = stringResource(R.string.password),
                placeholder = stringResource(R.string.password),
                initialText = "password123",
                isPassword = true,
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Lock, stringResource(R.string.password_icon))
                },
                onTextChange = { viewModel.onPasswordChange(it) })
            if (state.isRegister) {
                Input(
                    modifier = Modifier.padding(20.dp),
                    title = stringResource(R.string.email),
                    placeholder = stringResource(R.string.email),
                    initialText = "",
                    isPassword = false,
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Lock, stringResource(R.string.email))
                    },
                    onTextChange = { viewModel.onEmailChange(it) })

                RegisterButton(
                    username = state.username,
                    password = state.password,
                    email = state.email,
                    modifier = Modifier.padding(20.dp),
                    onRegisterComplete = { viewModel.onEvent(LoginEvents.UserRegisteredSuccessfully) })
            } else {
                Button(onClick = { viewModel.testRegistration() }) {
                    Text("test register")
                }
                LoginButton(
                    username = state.username,
                    password = state.password,
                    modifier = Modifier.padding(20.dp),
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
                        }
                        .padding(20.dp),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmPopup(
    modifier: Modifier = Modifier,
    onConfirmationCodeEntered: (String) -> Unit
) {
    val isLoading = remember { mutableStateOf(false) }
    val isDismiss = remember { mutableStateOf(false) }
    if (!isDismiss.value) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.zIndex(1f)) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            }
            Card(
                modifier = Modifier
                    .height(80.dp)
                    .width(120.dp)
                    .zIndex(2f),
            ) {
                Text(
                    stringResource(R.string.insert_email_code),
                    style = MaterialTheme.typography.bodyMedium
                )
                Input(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = stringResource(R.string.confirmation_code),
                    placeholder = stringResource(R.string.confirmation_code),
                    initialText = "",
                    isPassword = false,
                    onTextChange = {
                        onConfirmationCodeEntered(it)
                    })

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