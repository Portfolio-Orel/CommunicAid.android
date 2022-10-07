package com.orels.presentation.ui.unhandled_calls.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.orels.domain.model.entities.PhoneCall
import com.orels.domain.util.extension.formatDayAndHours
import com.orels.presentation.R
import com.orels.presentation.theme.MyMessagesTheme
import java.util.*

@Composable
fun UnhandledCallRow(
    modifier: Modifier = Modifier,
    phoneCall: PhoneCall,
    canDelete: Boolean = true,
    onDelete: (PhoneCall) -> Unit = { _ -> },
    onCall: (PhoneCall) -> Unit = { _ -> },
    onClick: (PhoneCall) -> Unit = { _ -> }
) {
    val context = LocalContext.current

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Phone,
            contentDescription = stringResource(R.string.call_unhandled_phone_call),
            modifier = Modifier
                .localPadding()
                .localIconSize()
                .clickable { onCall(phoneCall) },
            tint = MaterialTheme.colorScheme.onBackground
        )
        Column(modifier = Modifier
            .clickable { onClick(phoneCall) }) {
            Text(
                text = phoneCall.getName(LocalContext.current),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                phoneCall.startDate.formatDayAndHours(context),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        if(canDelete) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_remove_circle_24),
                contentDescription = stringResource(R.string.remove_unhandled_phone_call),
                modifier = Modifier
                    .localPadding()
                    .localIconSize()
                    .clickable { onDelete(phoneCall) },
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

private fun Modifier.localPadding(): Modifier = this.padding(horizontal = 12.dp)
private fun Modifier.localIconSize(): Modifier = this.size(34.dp)

@Preview(showBackground = true)
@Composable
fun DefaultPreview2() {
    MyMessagesTheme {
        UnhandledCallRow(
            phoneCall = PhoneCall(
                number = "0543056286",
                startDate = Date(),
                endDate = Date(),
                isWaiting = false,
                messagesSent = listOf()
            )
        )
    }
}