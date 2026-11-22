package com.orels.presentation.ui.components

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.orels.presentation.R

/**
 * Shared colors for the app's rounded, filled text fields: surfaceVariant container,
 * no indicator lines, and a soft error tint when [isError] is true.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun filledFieldColors(isError: Boolean = false): TextFieldColors =
    TextFieldDefaults.textFieldColors(
        containerColor = if (isError) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f)
        else MaterialTheme.colorScheme.surfaceVariant,
        textColor = MaterialTheme.colorScheme.onSurface,
        cursorColor = MaterialTheme.colorScheme.primary,
        errorCursorColor = MaterialTheme.colorScheme.error,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        placeholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
        unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
        unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Input(
    modifier: Modifier = Modifier,
    title: String = "",
    placeholder: String = "",
    initialText: String = "",
    minLines: Int = 1,
    maxLines: Int = 1,
    isError: Boolean = false,
    isPassword: Boolean = false,
    shouldFocus: Boolean = false,
    leadingIcon: @Composable (() -> Unit) = { },
    trailingIcon: @Composable (() -> Unit) = { },
    onTextChange: (String) -> Unit = {}
) {
    val value = remember { mutableStateOf(initialText) }
    val passwordVisible = rememberSaveable { mutableStateOf(false) }
    val lineHeight = 40
    val focusRequester = FocusRequester()

    var inputModifier = if(shouldFocus) Modifier.focusRequester(focusRequester) else Modifier

    inputModifier =
        if (maxLines == 1) inputModifier else inputModifier.height((lineHeight * minLines).dp)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                modifier = Modifier.padding(horizontal = 4.dp),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Box(modifier = Modifier.zIndex(1f)) {
            TextField(
                modifier = inputModifier
                    .fillMaxWidth()
                    .focusable(enabled = shouldFocus),
                value = value.value,
                onValueChange = {
                    value.value = it
                    onTextChange(it)
                },
                placeholder = {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                },
                textStyle = MaterialTheme.typography.bodyLarge,
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
                },
                isError = isError,
                shape = MaterialTheme.shapes.medium,
                colors = filledFieldColors(isError = isError)
            )
        }
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
