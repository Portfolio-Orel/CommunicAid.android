package com.orels.features.customer_status.presentation

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.orels.domain.util.common.Logger
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Orel Zilberman on 30/03/2024.
 */

@AndroidEntryPoint
class CustomerStateActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Logger.i("CustomerStateActivity onCreate")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(false)
            setTurnScreenOn(true)
        }
        setContent {
            CustomerStateScreen(
                onDismiss = {
                    finish()
                }
            )
        }
    }
}