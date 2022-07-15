import com.orelzman.mymessages.presentation.confirmation_screen.ConfirmationViewModel

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.orelzman.mymessages.R
import com.orelzman.mymessages.presentation.login.components.Input
import com.orelzman.mymessages.ui.theme.MyMessagesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationScreen(
    username: String,
    onDismiss: () -> Unit,
    onUserConfirmed: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ConfirmationViewModel = hiltViewModel()
) {
    val state = viewModel.state
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.zIndex(1f)) {
            MaterialTheme.colorScheme.primary
        }
        Card(
            modifier = Modifier
                .padding(16.dp)
                .height(220.dp)
                .width(300.dp)
                .zIndex(2f),
        ) {
            Icon(
                modifier = Modifier
                    .clickable {
                        onDismiss()
                    }
                    .height(60.dp)
                    .width(60.dp)
                    .padding(16.dp),
                imageVector = Icons.Filled.Close,
                contentDescription = "Close window",
                tint = MaterialTheme.colorScheme.onBackground
            )
            Text(
                stringResource(R.string.insert_email_code),
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            if (!state.isLoading) {
                Input(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = "",
                    placeholder = stringResource(R.string.confirmation_code),
                    initialText = "",
                    isPassword = false,
                    onTextChange = {
                        if (it.length == 6) {
                            viewModel.onCodeChange(
                                value = it,
                                username = username,
                                onUserConfirmed = onUserConfirmed
                            )
                        }
                    })
            } else {
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(16.dp)
                        .width(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MyMessagesTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            ConfirmationScreen(
                username = "",
                onDismiss = {},
                onUserConfirmed = {},
            )
        }
    }
}
