package com.orels.features.customer_status.presentation

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

/**
 * Created by Orel Zilberman on 30/03/2024.
 */

class CustomerStateActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(false)
            setTurnScreenOn(true)
        }
        setContent {
            PopupDialog()
        }
    }
}

@Composable
fun PopupDialog() {
    Dialog(onDismissRequest = {}) {
        // Box to create a small square area
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(200.dp) // Square size
                .background(Color.White) // Background color of the square
        ) {
            Text(
                text = "Hello there!",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}