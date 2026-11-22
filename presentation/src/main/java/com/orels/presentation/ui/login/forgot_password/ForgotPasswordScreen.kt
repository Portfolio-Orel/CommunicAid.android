package com.orels.presentation.ui.login.forgot_password

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import com.orels.presentation.theme.AppTheme
import com.orels.presentation.ui.login.components.AppBadge
import com.orels.presentation.ui.login.components.AuthPrimaryButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.orels.presentation.R
import com.orels.presentation.ui.login.components.AuthenticationInput

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
) {
    val state = viewModel.state

    var username by rememberSaveable { mutableStateOf("") }

    var code by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    Column {
        AnimatedVisibility(
            visible = (state.state is State.ForgotPassword),
            enter = slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(durationMillis = 250, easing = EaseInOut)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { fullWidth -> 2 * fullWidth },
                animationSpec = tween(durationMillis = 150, easing = EaseInOut)
            )
        ) {
            ForgotPasswordContent(
                isLoading = state.state.isLoading,
                username = username,
                onUsernameChange = { username = it },
                onForgotPassword = viewModel::onForgotPassword,
                isError = state.usernameField.isError
            )
        }

        AnimatedVisibility(
            visible = (state.state is State.ResetPassword),
            enter = slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(durationMillis = 250, easing = EaseInOut)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { fullWidth -> 2 * fullWidth },
                animationSpec = tween(durationMillis = 150, easing = EaseInOut)
            )
        ) {
            ResetPasswordContent(
                code = code,
                password = password,
                confirmPassword = confirmPassword,
                onCodeChange = { code = it },
                onPasswordChange = { password = it },
                onConfirmPasswordChange = { confirmPassword = it },
                isLoading = state.state.isLoading,
                onResetPassword = viewModel::onResetPassword,
                isPasswordError = state.passwordField.isError,
                isConfirmPasswordError = state.confirmPasswordField.isError,
                isCodeError = state.codeField.isError,
            )
        }

        AnimatedVisibility(
            visible = (state.state is State.Done),
            enter = slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(durationMillis = 250, easing = EaseInOut)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { fullWidth -> 2 * fullWidth },
                animationSpec = tween(durationMillis = 150, easing = EaseInOut)
            )
        ) {
            DoneContent(
                onDone = {
                    navController.popBackStack()
                }
            )
        }
        state.error?.let {
            Text(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                text = stringResource(it),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ForgotPasswordContent(
    isLoading: Boolean,
    username: String,
    onUsernameChange: (String) -> Unit,
    onForgotPassword: (String) -> Unit,
    isError: Boolean = false,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
    ) {
        AppBadge(modifier = Modifier.padding(horizontal = 16.dp))
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 6.dp),
            text = stringResource(R.string.did_forget_password),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(R.string.forgot_password_message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        AuthenticationInput(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 6.dp),
            placeholder = stringResource(R.string.username),
            value = username,
            imeAction = ImeAction.Done,
            onImeAction = {
                keyboardController?.hide()
                if (!isLoading) onForgotPassword(username)
            },
            onValueChange = { onUsernameChange(it) },
            isError = isError,
        )
        AuthPrimaryButton(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 16.dp),
            text = stringResource(R.string.reset_password),
            onClick = { if (!isLoading) onForgotPassword(username) },
            isLoading = isLoading,
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ResetPasswordContent(
    code: String,
    password: String,
    confirmPassword: String,
    onCodeChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    isLoading: Boolean,
    onResetPassword: ((code: String, password: String, confirmPassword: String) -> Unit)? = null,
    isCodeError: Boolean = false,
    isPasswordError: Boolean = false,
    isConfirmPasswordError: Boolean = false,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val passwordFocusRequest = remember { FocusRequester() }
    val confirmPasswordFocusRequest = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
    ) {

        AppBadge(modifier = Modifier.padding(horizontal = 16.dp))
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 6.dp),
            text = stringResource(R.string.change_the_password),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(R.string.insert_code_and_create_password),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        AuthenticationInput(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 6.dp),
            placeholder = stringResource(R.string.code),
            value = code,
            imeAction = ImeAction.Next,
            onImeAction = {
                passwordFocusRequest.requestFocus()
            },
            onValueChange = { if (!isLoading) onCodeChange(it) },
            isError = isCodeError,
        )

        AuthenticationInput(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            placeholder = stringResource(R.string.password),
            value = password,
            onValueChange = { if (!isLoading) onPasswordChange(it) },
            isPassword = true,
            imeAction = ImeAction.Next,
            onImeAction = {
                confirmPasswordFocusRequest.requestFocus()
            },
            focusRequester = passwordFocusRequest,
            isError = isPasswordError,
        )
        AuthenticationInput(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            placeholder = stringResource(R.string.confirm_password),
            value = confirmPassword,
            onValueChange = { if (!isLoading) onConfirmPasswordChange(it) },
            imeAction = ImeAction.Done,
            onImeAction = {
                keyboardController?.hide()
                if (!isLoading) onResetPassword?.invoke(code, password, confirmPassword)
            },
            isPassword = true,
            focusRequester = confirmPasswordFocusRequest,
            isError = isConfirmPasswordError,
        )
        AuthPrimaryButton(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 16.dp),
            text = stringResource(R.string.reset_password),
            onClick = { onResetPassword?.invoke(code, password, confirmPassword) },
            isLoading = isLoading,
        )
    }
}

@Composable
fun DoneContent(onDone: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(AppTheme.extended.successContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(48.dp),
                imageVector = Icons.Filled.Done,
                contentDescription = null,
                tint = AppTheme.extended.success
            )
        }
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 6.dp),
            text = stringResource(R.string.done_exclamation),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(R.string.your_password_has_been_changed),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        AuthPrimaryButton(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 24.dp),
            text = stringResource(R.string.lets_go),
            onClick = { onDone() },
        )
    }
}