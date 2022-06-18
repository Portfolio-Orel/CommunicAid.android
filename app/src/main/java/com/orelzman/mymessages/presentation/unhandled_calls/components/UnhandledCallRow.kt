package com.orelzman.mymessages.presentation.unhandled_calls.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_remove_circle_24),
            contentDescription = "Remove unhandled call",
            modifier = Modifier
                .size(30.dp)
                .clickable { onDelete(phoneCall) },
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.weight(1f))
        Column(modifier = Modifier.clickable { onClick(phoneCall) }) {
            Text(text = phoneCall.name)
            Text("14:12 • אתמול")
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_call_24),
            contentDescription = "Remove unhandled call",
            modifier = Modifier
                .size(30.dp)
                .clickable { onCall(phoneCall) },
            tint = MaterialTheme.colorScheme.surface
        )
    }
}