package com.orelzman.mymessages.presentation.login.register_button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orelzman.mymessages.R
import com.orelzman.mymessages.ui.theme.Shapes

@Composable
fun RegisterButton(
    username: String,
    password: String,
    email: String,
    onRegisterComplete: () -> Unit,
    modifier: Modifier = Modifier,
    isSaveCredentials: Boolean = false,
    viewModel: RegisterButtonViewModel = hiltViewModel()
) {
    val state = viewModel.state

    if (state.isRegisterAttempt && (!state.isLoading && state.isRegisterCompleted)) {
        onRegisterComplete()
    }

    Surface(
        modifier = modifier.clickable(
            enabled = !state.isLoading,
            onClick = {
                viewModel.register(
                    username = username,
                    password = password,
                    email = email,
                    isSaveCredentials = isSaveCredentials
                )
            }
        ),
        shape = Shapes.medium,
        border = BorderStroke(width = 1.dp, color = Color.LightGray),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .padding(
                    start = 12.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = if (state.isLoading) stringResource(R.string.registering) else stringResource(
                    R.string.register
                )
            )
            if (state.isLoading) {
                Spacer(modifier = Modifier.width(16.dp))
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(16.dp)
                        .width(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}