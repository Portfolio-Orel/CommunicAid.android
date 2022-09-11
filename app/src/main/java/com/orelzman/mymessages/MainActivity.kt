package com.orelzman.mymessages

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.orelzman.mymessages.domain.managers.system_service.SystemServiceManager
import com.orelzman.mymessages.presentation.my_messages.MyMessagesApp
import com.orelzman.mymessages.ui.theme.MyMessagesTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@ExperimentalPermissionsApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var systemServiceManager: SystemServiceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyMessagesTheme {
                val systemUiController = rememberSystemUiController()
                systemUiController.setStatusBarColor(
                    color = MaterialTheme.colorScheme.background,
                    darkIcons = MaterialTheme.colorScheme.background.luminance() > 0.5f
                )
                window.statusBarColor = MaterialTheme.colorScheme.background.toArgb()


                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val permissionsState = rememberMultiplePermissionsState(
                        permissions = listOf(
                            Manifest.permission.READ_CALL_LOG,
                            Manifest.permission.CALL_PHONE,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.READ_CONTACTS
                        )
                    )
                    val lifecycleOwner = LocalLifecycleOwner.current

                    DisposableEffect(
                        key1 = lifecycleOwner,
                        effect = {
                            val observer = LifecycleEventObserver { _, event ->
                                if (event == Lifecycle.Event.ON_START) {
                                    permissionsState.launchMultiplePermissionRequest()
                                }
                            }
                            lifecycleOwner.lifecycle.addObserver(observer)

                            onDispose {
                                lifecycleOwner.lifecycle.removeObserver(observer)
                            }
                        }
                    ) // ToDo: add assurance that the permissions were granted

                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MyMessagesApp()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        systemServiceManager.onResume()
    }

    override fun onPause() {
        super.onPause()
        systemServiceManager.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        systemServiceManager.onDestroy()
    }
}