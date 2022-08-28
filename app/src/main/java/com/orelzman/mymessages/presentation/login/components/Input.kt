package com.orelzman.mymessages.presentation.login.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.orelzman.mymessages.R

@Composable
fun Input(
    modifier: Modifier = Modifier,
    title: String = "",
    placeholder: String = "",
    initialText: String = "",
    minLines: Int = 1,
    maxLines: Int = 1,
    isPassword: Boolean = false,
    leadingIcon: @Composable (() -> Unit) = { },
    trailingIcon: @Composable (() -> Unit) = { },
    onTextChange: (String) -> Unit = {}
) {
    val value = remember { mutableStateOf(initialText) }
    val passwordVisible = rememberSaveable { mutableStateOf(false) }
    val textFieldModifier: Modifier =
        if (maxLines == 1) Modifier else Modifier.height((40 * minLines).dp)

    Column(modifier = modifier.fillMaxWidth()) {
        Text(title)
        OutlinedTextField(
            modifier = textFieldModifier.fillMaxWidth(),
            value = value.value,
            onValueChange = {
                value.value = it
                onTextChange(it)
            },
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            },
            singleLine = maxLines == 1,
            visualTransformation = if (isPassword && !passwordVisible.value) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions(
                keyboardType = KeyboardType.Text
            ),
            trailingIcon = {
                if (isPassword) {
                    PasswordIcon(
                        passwordVisible = passwordVisible.value,
                        onClick = { passwordVisible.value = !passwordVisible.value })
                } else {
                    trailingIcon()
                }
            }

        )
    }
}

@Composable
private fun PasswordIcon(
    passwordVisible: Boolean,
    onClick: () -> Unit
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
