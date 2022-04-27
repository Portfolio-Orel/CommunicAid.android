package com.orelzman.mymessages.presentation.main.components

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.orelzman.mymessages.data.dto.Message
import com.orelzman.mymessages.ui.theme.MyMessagesTheme
import com.orelzman.mymessages.util.extension.advancedShadow

@Composable
fun MessageView(
    message: Message,
    modifier: Modifier = Modifier,
    onClick: (Message, Context) -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .advancedShadow(color = Color.Red)
        ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(0.7f)
                .clip(RoundedCornerShape(4.dp))
                .clickable {
                    onClick(message, context)
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = message.messageShortTitle)
        }
        Text(text = message.messageTitle)
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