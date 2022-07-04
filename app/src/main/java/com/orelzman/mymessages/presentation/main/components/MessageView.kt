package com.orelzman.mymessages.presentation.main.components

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.orelzman.mymessages.domain.model.entities.Message
import com.orelzman.mymessages.ui.theme.MyMessagesTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MessageView(
    message: Message,
    modifier: Modifier = Modifier,
    onClick: (Message, Context) -> Unit,
    onLongClick: (Message, Context) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxHeight(0.7f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .combinedClickable(
                    onClick = { onClick(message, context) },
                    onLongClick = { onLongClick(message, context) }
                )

                .background(MaterialTheme.colorScheme.secondary),
            elevation = CardDefaults.elevatedCardElevation(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
                    .padding(12.dp)
            ) {
                Text(
                    text = message.shortTitle,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        Text(
            text = message.title,
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
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MessageView(
                    message = Message.default,
                    modifier = Modifier
                        .height(80.dp)
                        .width(70.dp),
                    onClick = { _, _ -> }
                )
            }
        }
    }
}