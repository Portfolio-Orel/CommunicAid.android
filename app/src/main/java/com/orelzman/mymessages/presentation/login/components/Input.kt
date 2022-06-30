package com.orelzman.mymessages.presentation.login.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun Input(
    modifier: Modifier = Modifier,
    title: String = "",
    placeholder: String = "",
    initialText: String = "",
    isPassword: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    onTextChange: (String) -> Unit = {}
) {
    val value = remember { mutableStateOf(initialText) }
    val passwordVisible = rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(title)
        TextField(
            value = value.value,
            onValueChange = {
                value.value = it
                onTextChange(it)
            },
            placeholder = { Text(placeholder) },
            singleLine = true,
            visualTransformation = if (isPassword && !passwordVisible.value) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions(
                keyboardType = KeyboardType.Text
            ),
            trailingIcon = {
//                val image = if (passwordVisible.value)
//                    Icons.Filled.
//                else Icons.Filled.VisibilityOff
//                // Please provide localized description for accessibility services
//                val description = if (passwordVisible.value) stringResource(R.string.hide_password) else stringResource(R.string.show_password)
//
//                IconButton(onClick = { passwordVisible != passwordVisible }) {
//                    Icon(imageVector = image, description)
//                }
            })
    }
}
