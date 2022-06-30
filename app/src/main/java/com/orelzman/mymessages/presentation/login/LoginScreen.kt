package com.orelzman.mymessages.presentation.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orelzman.mymessages.R
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

    Column {
        Input(
            modifier = Modifier.padding(20.dp),
            title = stringResource(R.string.user_name),
            placeholder = stringResource(R.string.user_name),
            initialText = "",
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
            initialText = "",
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
            LoginButton(
                username = state.username,
                password = state.password,
                modifier = Modifier.padding(20.dp),
                onLoginComplete = { viewModel.onEvent(LoginEvents.UserLoggedInSuccessfully(it)) })
            Text(
                stringResource(R.string.register),
                modifier = Modifier.clickable {
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

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MyMessagesTheme {
        LoginScreen(
            navigator = DefaultDestinationNavigator()
        )
    }
}