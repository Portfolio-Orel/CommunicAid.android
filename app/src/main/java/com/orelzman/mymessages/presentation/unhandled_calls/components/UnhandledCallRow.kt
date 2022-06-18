package com.orelzman.mymessages.presentation.unhandled_calls.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.orelzman.mymessages.R
import com.orelzman.mymessages.data.dto.PhoneCall

@Composable
fun UnhandledCallRow(
    modifier: Modifier = Modifier,
    phoneCall: PhoneCall,
    onDelete: (PhoneCall) -> Unit = { _ -> },
    onCall: (PhoneCall) -> Unit = { _ -> },
    onClick: (PhoneCall) -> Unit = { _ -> }
) {
    Row(
        modifier = modifier
            .fillMaxSize()
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_remove_circle_24),
            contentDescription = "Remove unhandled call",
            tint = MaterialTheme.colorScheme.error
        )
        Divider()
        Column {
            Text(text = phoneCall.name)
            Text("14:12 • אתמול")
        }
        Divider()
        Icon(
            painter = painterResource(id = R.drawable.ic_call_24),
            contentDescription = "Remove unhandled call",
            tint = MaterialTheme.colorScheme.surface
        )
    }
}