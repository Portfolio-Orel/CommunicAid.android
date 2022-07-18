package com.orelzman.mymessages.presentation.components.logout_button

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orelzman.mymessages.R

@Composable
fun LogoutButton(
    onLogoutComplete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LogoutButtonViewModel = hiltViewModel(),
) {
    val state = viewModel.state

    Button(
        modifier = modifier,
        onClick = {
            viewModel.logout(onLogoutComplete = onLogoutComplete)
        },
    ) {
        Text(
            text = if (state.isLoading) stringResource(R.string.loging_out) else stringResource(
                R.string.logout
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