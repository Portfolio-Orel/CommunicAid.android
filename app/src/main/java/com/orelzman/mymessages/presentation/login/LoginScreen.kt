package com.orelzman.mymessages.presentation.login

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.orelzman.mymessages.R
import com.orelzman.mymessages.presentation.login.components.Input
import com.orelzman.mymessages.ui.theme.MyMessagesTheme
import com.orelzman.mymessages.util.extension.DefaultDestinationNavigator
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination(start = true)
fun LoginScreen(
    navigator: DestinationsNavigator,
//    viewModel: LoginViewModel = hiltViewModel()
) {
//    val state = viewModel.state

    Column {
        Input(
            title = "User name",
            placeholder = "User name",
            initialText = "",
            isPassword = false,
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Person, stringResource(R.string.user_name))
            },
            onTextChange = {}
        )
        Input(
            title = "Password",
            placeholder = "Password",
            initialText = "",
            isPassword = true,
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Lock, stringResource(R.string.password_icon))
            },
            onTextChange = {})


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