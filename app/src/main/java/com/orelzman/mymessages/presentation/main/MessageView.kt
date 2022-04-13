package com.orelzman.mymessages.presentation.main

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.orelzman.mymessages.domain.model.Message
import com.orelzman.mymessages.ui.theme.MyMessagesTheme

@Composable
fun MessageView(
    message: Message,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .clickable { },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .border(width = 1.dp, color = Color.Blue)
                .height(IntrinsicSize.Max)
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(text = message.shortTitle)
        }
        Text(text = message.title)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyMessagesTheme {
        Column(modifier = Modifier
            .height(80.dp)
            .width(70.dp)) {
            MessageView(message = Message(title = "title", shortTitle = "st", body = "body"))
        }
    }
}