package com.orelzman.mymessages

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.orelzman.mymessages.presentation.details_folder.DetailsFolderScreen
import com.orelzman.mymessages.presentation.details_message.DetailsMessageScreen
import com.orelzman.mymessages.presentation.login.LoginScreen
import com.orelzman.mymessages.presentation.main.MainScreen
import com.orelzman.mymessages.presentation.stats.StatsScreen
import com.orelzman.mymessages.presentation.unhandled_calls.UnhandledCallsScreen
import com.orelzman.mymessages.ui.theme.MyMessagesTheme
import com.orelzman.mymessages.util.Screen
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalPermissionsApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyMessagesTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val permissionsState = rememberMultiplePermissionsState(
                        permissions = listOf(
                            Manifest.permission.READ_CALL_LOG,
                            Manifest.permission.SEND_SMS,
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

                    val navHostController = rememberNavController()
                    NavHost(navController = navHostController, startDestination = "main") {
                        composable(route = Screen.Main.route) { MainScreen() }
                        composable(route = Screen.Login.route) { LoginScreen() }
                        composable(route = Screen.UnhandledCalls.route) { UnhandledCallsScreen() }
                        composable(route = Screen.Statistics.route) { StatsScreen() }
                        composable(
                            route = Screen.DetailsMessage.route + "/{messageId}",
                            arguments = listOf(
                                navArgument("messageId") {
                                    type = NavType.StringType
                                    defaultValue = null
                                    nullable = true
                                }
                            )
                        ) { DetailsMessageScreen() }
                        composable(
                            route = Screen.DetailsFolder.route + "/{folderId}",
                            arguments = listOf(
                                navArgument("folderId") {
                                    type = NavType.StringType
                                    defaultValue = null
                                    nullable = true
                                }
                            )) { DetailsFolderScreen() }
                    }

                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MyMessagesApp(navController = navHostController)
                    }
                }
            }
        }
    }

}