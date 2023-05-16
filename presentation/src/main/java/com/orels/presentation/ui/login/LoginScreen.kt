package com.orels.presentation.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.orels.domain.util.Screen
import com.orels.presentation.R
import com.orels.presentation.theme.fontsVarelaround
import com.orels.presentation.theme.noRippleClickable
import com.orels.presentation.ui.login.components.AuthenticationInput

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val state = viewModel.state
    val keyboardController = LocalSoftwareKeyboardController.current

    val passwordFocusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            text = stringResource(R.string.welcome_back),
            style = MaterialTheme.typography.headlineLarge.copy(fontFamily = fontsVarelaround),
            fontWeight = FontWeight.Bold,
        )

        AuthenticationInput(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            placeholder = stringResource(R.string.username),
            value = state.username,
            onValueChange = viewModel::onUsernameChange,
            imeAction = ImeAction.Next,
            onImeAction = { passwordFocusRequester.requestFocus() },
            isError = state.usernameField.isError,
        )
        AuthenticationInput(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            placeholder = stringResource(R.string.password),
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            onImeAction = {
                keyboardController?.hide()
                viewModel.login()
            },
            imeAction = ImeAction.Done,
            isPassword = true,
            focusRequester = passwordFocusRequester,
            isError = state.passwordField.isError,
        )
        Text(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .noRippleClickable {
                    navController.navigate(Screen.ForgotPassword.route)
                },
            text = stringResource(R.string.did_forget_password),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
        )

        state.error?.let {
            Text(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                text = stringResource(it),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.dont_have_an_account),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Light,
            )
            Text(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .noRippleClickable {
                        navController.navigate(Screen.Register.route)
                    },
                text = stringResource(R.string.sign_up),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
            )
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 8.dp),
            onClick = viewModel::login,
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onBackground,
                contentColor = MaterialTheme.colorScheme.background,
            ),
        ) {
            if (state.isLoading) {
                Loading(size = 24.dp, color = MaterialTheme.colorScheme.background, width = 1.dp)
            } else {
                Text(
                    text = stringResource(R.string.login),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Normal,
                )
            }
        }
    }
}