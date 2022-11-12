package com.orels.presentation.ui.login

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.orels.presentation.R
import com.orels.presentation.ui.components.confirmation_pop_up.ConfirmationScreen
import com.orels.presentation.ui.components.login_button.LoginButton
import com.orels.presentation.ui.components.register_button.RegisterButton
import com.orels.presentation.ui.login.components.Input
import com.orels.presentation.ui.login.components.forgot_password.ForgotPasswordComponent
import com.orels.presentation.ui.main.components.ActionButton

@ExperimentalMaterial3Api
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state = viewModel.state
    if (state.isLoading) {
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
                color = MaterialTheme.colorScheme.inversePrimary
            )
        }
    } else {
        when (state.event) {
            Event.NotAuthorized -> ContentView(viewModel = viewModel)
            Event.RegistrationRequired -> RegistrationScreen(onRegister = { firstName, lastName ->
                viewModel.createUser(
                    firstName = firstName,
                    lastName = lastName
                )
            })
            Event.Authorized -> {}
        }
    }
}

@ExperimentalMaterial3Api
@Composable
private fun ContentView(viewModel: LoginViewModel) {
    val state = viewModel.state
    val context = LocalContext.current

    if (state.showCodeConfirmation) {
        Column(
            modifier =
            Modifier
                .zIndex(2f)
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f))
        ) {
            ConfirmationScreen(
                username = state.username,
                password = state.password,
                onDismiss = {
                    viewModel.hideRegistrationConfirmation()
                }, onUserConfirmed = {
                    viewModel.onEvent(LoginEvents.OnLoginCompleted(true, null))
                })
        }
    }
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
                title = stringResource(R.string.username),
                placeholder = stringResource(R.string.username),
                initialText = "",
                isPassword = false,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        stringResource(R.string.username)
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
            }
            ForgotPasswordComponent()
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
                },
                onLoginClick = { viewModel.onLoginClick() }
            )
            if ((context as? Activity) != null) {
                GoogleButton(onClick = { viewModel.googleAuth(activity = context) })
            }
            Text(
                text = stringResource(state.error ?: R.string.empty_string),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun GoogleButton(onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center)
    {
        Button(
            onClick = onClick,
            modifier = Modifier.size(75.dp),
            shape = CircleShape,
            contentPadding = PaddingValues(),
            colors = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.background,
                containerColor = MaterialTheme.colorScheme.onBackground
            )
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo_google),
                contentDescription = ""
            )
        }
    }
}

@Composable
fun RegistrationScreen(
    onRegister: (firstName: String, lastName: String) -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var firstNameError by remember { mutableStateOf(false) }
    var lastName by remember { mutableStateOf("") }
    var lastNameError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
    ) {
        Text(
            text = stringResource(R.string.what_is_your_name),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Input(
            title = stringResource(R.string.first_name),
            minLines = 1,
            maxLines = 1,
            isError = firstNameError,
            isPassword = false,
            onTextChange = {
                firstName = it
                firstNameError = false
            }
        )
        Input(
            title = stringResource(R.string.last_name),
            minLines = 1,
            maxLines = 1,
            isError = lastNameError,
            isPassword = false,
            onTextChange = {
                lastName = it
                lastNameError = false
            }
        )
        Spacer(modifier = Modifier.height(32.dp))
        ActionButton(
            onClick = {
                if (firstName.isNotBlank() && lastName.isNotBlank()) {
                    onRegister(firstName, lastName)
                } else {
                    firstNameError = firstName.isBlank()
                    lastNameError = lastName.isBlank()
                }
            }, text = stringResource(R.string.finish)
        )
    }
}
