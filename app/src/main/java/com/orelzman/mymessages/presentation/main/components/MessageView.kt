package com.orelzman.mymessages.presentation.main.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.orelzman.mymessages.data.dto.Message
import com.orelzman.mymessages.ui.theme.MyMessagesTheme

@Composable
fun MessageView(
    message: Message,
    modifier: Modifier = Modifier,
    onClick: (Message, Context) -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(0.7f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .clickable {
                    onClick(message, context)
                }
                .background(MaterialTheme.colorScheme.secondary),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = message.messageShortTitle,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Text(
            text = message.messageTitle,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyMessagesTheme {
        Column(
            modifier = Modifier
                .height(80.dp)
                .width(70.dp)
        ) {
            MessageView(
                message = Message.default,
                onClick = { _, _ -> }
            )
        }
    }
}