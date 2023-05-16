package com.orels.presentation.ui.my_messages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.orels.domain.model.entities.UserState
import com.orels.domain.util.Screen
import com.orels.presentation.ui.components.CustomScaffold
import com.orels.presentation.ui.components.bottom_bar.BottomBar
import com.orels.presentation.ui.components.top_app_bar.TopAppBar
import com.orels.presentation.ui.details_folder.DetailsFolderScreen
import com.orels.presentation.ui.details_message.DetailsMessageScreen
import com.orels.presentation.ui.login.LoginScreen
import com.orels.presentation.ui.login.forgot_password.ForgotPasswordScreen
import com.orels.presentation.ui.main.MainScreen
import com.orels.presentation.ui.register.RegisterScreen
import com.orels.presentation.ui.settings.SettingsScreen
import com.orels.presentation.ui.statistics.StatisticsScreen
import com.orels.presentation.ui.unhandled_calls.UnhandledCallsScreen

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class
)
@Composable
fun MyMessagesApp(
    viewModel: MyMessagesViewModel = hiltViewModel(),
) {
    val state = viewModel.state

    CompositionLocalProvider(
        LocalOverscrollConfiguration provides null
    ) {
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(48.dp)
                        .width(48.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            AnimatedVisibility(
                visible = (state.authState != UserState.Authorized),
                enter = fadeIn(
                    animationSpec = tween(durationMillis = 250, easing = EaseInOut)
                ),
                exit = fadeOut(
                    animationSpec = tween(durationMillis = 250, easing = EaseInOut)
                )
            ) {
                val navHostControllerNotAuthorized = rememberNavController()
                val navControllerNotAuthorized = navHostControllerNotAuthorized as NavController

                NavHost(
                    modifier = Modifier.padding(
                    ),
                    navController = navHostControllerNotAuthorized,
                    startDestination = Screen.Login.route
                ) {
                    composable(route = Screen.Login.route) { LoginScreen(navController = navControllerNotAuthorized) }
                    composable(route = Screen.ForgotPassword.route) {
                        ForgotPasswordScreen(navController = navControllerNotAuthorized)
                    }
                    composable(route = Screen.Register.route) {
                        RegisterScreen(navController = navControllerNotAuthorized)
                    }
                }
            }
            AnimatedVisibility(
                visible = state.authState == UserState.Authorized,
                enter = fadeIn(
                    animationSpec = tween(durationMillis = 250, easing = EaseInOut)
                ),
                exit = fadeOut(
                    animationSpec = tween(durationMillis = 250, easing = EaseInOut)
                )
            ) {
                val navHostControllerAuthorized = rememberNavController()
                val navControllerAuthorized = navHostControllerAuthorized as NavController

                CustomScaffold(
                    navController = navHostControllerAuthorized,
                    topBar = { TopAppBar(navController = navControllerAuthorized) },
                    bottomBar = { BottomBar(navController = it) },
                ) {
                    NavHost(
                        modifier = Modifier.padding(
                            top = it.calculateTopPadding(),
                            bottom = it.calculateBottomPadding(),
                            end = it.calculateEndPadding(LayoutDirection.Rtl),
                            start = it.calculateStartPadding(LayoutDirection.Rtl)
                        ),
                        navController = navHostControllerAuthorized,
                        startDestination = Screen.Main.route
                    ) {
                        composable(route = Screen.Main.route) { MainScreen(navController = navHostControllerAuthorized) }
                        composable(route = Screen.Login.route) { LoginScreen(navController = navControllerAuthorized) }
                        composable(route = Screen.UnhandledCalls.route) { UnhandledCallsScreen() }
                        composable(route = Screen.Statistics.route) { StatisticsScreen() }
                        composable(route = Screen.Settings.route) { SettingsScreen() }
                        composable(route = Screen.ForgotPassword.route) {
                            ForgotPasswordScreen(navController = navControllerAuthorized)
                        }
                        composable(route = Screen.DetailsMessage.route) {
                            DetailsMessageScreen(
                                navController = navControllerAuthorized
                            )
                        }
                        composable(
                            route = Screen.DetailsMessage.route + "/{messageId}",
                            arguments = listOf(
                                navArgument("messageId") {
                                    type = NavType.StringType
                                    defaultValue = ""
                                }
                            )
                        ) { navBackStack ->
                            DetailsMessageScreen(
                                navController = navControllerAuthorized,
                                messageId = navBackStack.arguments?.getString("messageId")
                            )
                        }
                        composable(route = Screen.DetailsFolder.route) {
                            DetailsFolderScreen(navController = navControllerAuthorized)
                        }
                        composable(
                            route = Screen.DetailsFolder.route + "/{folderId}",
                            arguments = listOf(
                                navArgument("folderId") {
                                    type = NavType.StringType
                                    defaultValue = ""
                                }
                            )) { navBackStack ->
                            DetailsFolderScreen(
                                navController = navControllerAuthorized,
                                folderId = navBackStack.arguments?.getString("folderId")
                            )
                        }
                    }
                }
            }
        }
    }
}