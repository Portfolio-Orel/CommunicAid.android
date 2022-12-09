package com.orels.presentation.ui.login.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.orels.presentation.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticationInput(
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    maxLines: Int = 1,
    minLines: Int = 1,
    value: String = "",
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    onImeAction: (KeyboardActionScope.() -> Unit)? = null,
    imeAction: ImeAction = ImeAction.Done,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    focusRequester: FocusRequester = FocusRequester(),
    isError: Boolean = false,
) {
    var text by remember { mutableStateOf(value) }
    val passwordVisible = rememberSaveable { mutableStateOf(false) }

    Row(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier
                .focusRequester(focusRequester)
                .fillMaxWidth()
                .drawBehind {
                    drawRect(
                        color = Color.Transparent,
                        size = size,
                    )
                },
            value = text,
            onValueChange = {
                val strippedText = it.stripSpacesTabsAndNewLines()
                text = strippedText
                onValueChange(strippedText)
            },
            keyboardActions = KeyboardActions(
                onDone = onImeAction,
                onGo = onImeAction,
                onNext = onImeAction,
                onPrevious = onImeAction,
                onSearch = onImeAction,
                onSend = onImeAction,

                ),
            visualTransformation = if (isPassword && !passwordVisible.value) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = if (isPassword) KeyboardOptions(
                imeAction = imeAction,
                keyboardType = KeyboardType.Password
            ) else KeyboardOptions(
                imeAction = imeAction,
                keyboardType = keyboardType
            ),
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            },

            maxLines = maxLines,
            minLines = minLines,
            shape = MaterialTheme.shapes.medium,
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                errorIndicatorColor = MaterialTheme.colorScheme.error,
                textColor = MaterialTheme.colorScheme.onBackground,
                containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.6f),
                cursorColor = MaterialTheme.colorScheme.onBackground,
                focusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            ),
            leadingIcon = leadingIcon,
            trailingIcon = {
                if (isPassword) {
                    PasswordIcon(
                        passwordVisible = passwordVisible.value,
                        onClick = { passwordVisible.value = !passwordVisible.value })
                } else {
                    trailingIcon?.invoke()
                }
            },
            isError = isError,
        )
    }
}

@Composable
private fun PasswordIcon(
    passwordVisible: Boolean,
    onClick: () -> Unit,
) {
    val image = if (passwordVisible)
        painterResource(id = R.drawable.ic_visibility_off_24)
    else painterResource(id = R.drawable.ic_visibility_24)
    val description =
        if (passwordVisible) stringResource(R.string.hide_password) else stringResource(R.string.show_password)

    IconButton(onClick = onClick) {
        Icon(painter = image, description)
    }
}

private fun String.stripSpacesTabsAndNewLines(): String =
    this.replace("\\s".toRegex(), "")