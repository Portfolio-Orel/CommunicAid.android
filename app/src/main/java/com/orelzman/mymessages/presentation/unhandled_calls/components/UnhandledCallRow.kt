package com.orelzman.mymessages.presentation.unhandled_calls.components

import android.content.Context
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.orelzman.mymessages.R
import com.orelzman.mymessages.domain.model.entities.PhoneCall
import com.orelzman.mymessages.ui.theme.MyMessagesTheme
import com.orelzman.mymessages.util.extension.getDayFormatted
import com.orelzman.mymessages.util.extension.getHourHHMM
import java.util.*

@Composable
fun UnhandledCallRow(
    modifier: Modifier = Modifier,
    phoneCall: PhoneCall,
    onDelete: (PhoneCall) -> Unit = { _ -> },
    onCall: (PhoneCall) -> Unit = { _ -> },
    onClick: (PhoneCall) -> Unit = { _ -> }
) {
    val context = LocalContext.current

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.inverseSurface),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Phone,
            contentDescription = "Remove unhandled call",
            modifier = Modifier
                .localPadding()
                .localIconSize()
                .clickable { onCall(phoneCall) },
            tint = MaterialTheme.colorScheme.surface
        )
        Column(modifier = Modifier
            .clickable { onClick(phoneCall) }) {
            Text(
                text = phoneCall.getName(LocalContext.current),
                color = MaterialTheme.colorScheme.surface,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                phoneCall.startDate.format(context),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_remove_circle_24),
            contentDescription = "Remove unhandled call",
            modifier = Modifier
                .localPadding()
                .localIconSize()
                .clickable { onDelete(phoneCall) },
            tint = MaterialTheme.colorScheme.error
        )
    }
}

private fun Modifier.localPadding(): Modifier = this.padding(horizontal = 12.dp)
private fun Modifier.localIconSize(): Modifier = this.size(34.dp)

private fun Date.format(context: Context): String =
    "${getDayFormatted(context)} • ${getHourHHMM()}"


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