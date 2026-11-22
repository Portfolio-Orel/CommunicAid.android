package com.orels.presentation.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.orels.presentation.ui.login.components.AppBadge
import com.orels.presentation.ui.login.components.AuthPrimaryButton
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
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
    ) {
        AppBadge(modifier = Modifier.padding(horizontal = 16.dp))
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 20.dp),
            text = stringResource(R.string.welcome_back),
            style = MaterialTheme.typography.headlineLarge.copy(fontFamily = fontsVarelaround),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )

        AuthenticationInput(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
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
                .padding(horizontal = 16.dp, vertical = 6.dp),
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
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .noRippleClickable {
                    navController.navigate(Screen.ForgotPassword.route)
                },
            text = stringResource(R.string.did_forget_password),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
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
/* Registration is disabled for now */
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.Center,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                text = stringResource(R.string.dont_have_an_account),
//                style = MaterialTheme.typography.bodySmall,
//                fontWeight = FontWeight.Light,
//            )
//            Text(
//                modifier = Modifier
//                    .padding(horizontal = 8.dp)
//                    .noRippleClickable { },
//                text = stringResource(R.string.sign_up),
//                style = MaterialTheme.typography.bodySmall,
//                fontWeight = FontWeight.Bold,
//            )
//        }
        AuthPrimaryButton(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 8.dp),
            text = stringResource(R.string.login),
            onClick = viewModel::login,
            isLoading = state.isLoading,
        )
    }
}