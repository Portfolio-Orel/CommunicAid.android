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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.orels.presentation.R
import com.orels.presentation.theme.MyMessagesTheme
import com.orels.presentation.ui.components.confirmation_pop_up.ConfirmationScreen
import com.orels.presentation.ui.components.login_button.LoginButton
import com.orels.presentation.ui.components.register_button.RegisterButton
import com.orels.presentation.ui.login.components.Input
import com.orels.presentation.ui.login.components.forgot_password.ForgotPasswordComponent

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
        ContentView(viewModel = viewModel)
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

@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MyMessagesTheme {
        LoginScreen()
    }
}