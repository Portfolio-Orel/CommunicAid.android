package com.orelzman.mymessages

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.orelzman.mymessages.domain.model.Folder
import com.orelzman.mymessages.domain.model.Message
import com.orelzman.mymessages.presentation.main.FolderView
import com.orelzman.mymessages.presentation.main.MessageView
import com.orelzman.mymessages.ui.theme.MyMessagesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyMessagesTheme {
                Surface(color = MaterialTheme.colors.background) {
                    MessageView(
                        message = Message(
                            title = "title",
                            shortTitle = "st",
                            body = "body"
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyMessagesTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            MessageView(
                message = Message(
                    title = "title",
                    shortTitle = "st",
                    body = "body",
                ),
                modifier = Modifier
                    .height(80.dp)
                    .width(70.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            FolderView(
                folder = Folder(title = "folder"),
                isSelected = false,
                modifier = Modifier
                    .width(80.dp)
                    .height(50.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            FolderView(
                folder = Folder(title = "folderSelected"),
                isSelected = true,
                modifier = Modifier
                    .width(80.dp)
                    .height(50.dp)
            )
        }
    }
}