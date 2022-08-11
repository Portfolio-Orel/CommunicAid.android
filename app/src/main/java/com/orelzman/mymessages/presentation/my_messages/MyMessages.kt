package com.orelzman.mymessages.presentation.my_messages

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.orelzman.mymessages.R
import com.orelzman.mymessages.domain.util.Screen
import com.orelzman.mymessages.presentation.components.CustomScaffold
import com.orelzman.mymessages.presentation.components.bottom_bar.BottomBar
import com.orelzman.mymessages.presentation.components.multi_fab.MiniFloatingAction
import com.orelzman.mymessages.presentation.components.multi_fab.MultiFab
import com.orelzman.mymessages.presentation.components.top_app_bar.TopAppBar
import com.orelzman.mymessages.presentation.details_folder.DetailsFolderScreen
import com.orelzman.mymessages.presentation.details_message.DetailsMessageScreen
import com.orelzman.mymessages.presentation.login.LoginScreen
import com.orelzman.mymessages.presentation.main.MainScreen
import com.orelzman.mymessages.presentation.settings.SettingsScreen
import com.orelzman.mymessages.presentation.statistics.StatisticsScreen
import com.orelzman.mymessages.presentation.unhandled_calls.UnhandledCallsScreen

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class
)
@Composable
fun MyMessagesApp(
    viewModel: MyMessagesViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val navHostController = rememberNavController()
    val navController = navHostController as NavController
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
            if (!state.isAuthorized) {
                LoginScreen()
            } else {
                CustomScaffold(
                    navController = navHostController,
                    topBar = { TopAppBar(navController = navController) },
                    bottomBar = { BottomBar(navController = it) },
                    floatingActionButton = {
                        Fab(
                            navController = navController,
                            navHostController = navHostController,
                            signOut = {
                                viewModel.signOut()
                               navController.navigate(Screen.Main.route) {
                                   popUpTo(navHostController.graph.findStartDestination().id) {
                                       inclusive = true
                                   }
                               }
                            }
                        )
                    },
                ) {
                    NavHost(
                        modifier = Modifier.padding(
                            top = it.calculateTopPadding(),
                            bottom = it.calculateBottomPadding(),
                            end = it.calculateEndPadding(LayoutDirection.Rtl),
                            start = it.calculateStartPadding(LayoutDirection.Rtl)
                        ),
                        navController = navHostController, startDestination = Screen.Main.route
                    ) {
                        composable(route = Screen.Main.route) { MainScreen(navController = navHostController) }
                        composable(route = Screen.Login.route) { LoginScreen() }
                        composable(route = Screen.UnhandledCalls.route) { UnhandledCallsScreen() }
                        composable(route = Screen.Statistics.route) { StatisticsScreen() }
                        composable(route = Screen.Settings.route) { SettingsScreen() }
                        composable(route = Screen.DetailsMessage.route) {
                            DetailsMessageScreen(
                                navController = navHostController
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
                                navController = navHostController,
                                messageId = navBackStack.arguments?.getString("messageId")
                            )
                        }
                        composable(route = Screen.DetailsFolder.route) {
                            DetailsFolderScreen(navController = navHostController)
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
                                navController = navHostController,
                                folderId = navBackStack.arguments?.getString("folderId")
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Fab(
    navController: NavController,
    navHostController: NavHostController,
    signOut: () -> Unit
) {
    MultiFab(
        fabs = listOf(
            MiniFloatingAction(
                action = { signOut() },
                icon = painterResource(id = R.drawable.ic_login),
                description = stringResource(R.string.sign_out)
            ),
            MiniFloatingAction(
                action = {
                    navController.navigate(
                        Screen.DetailsFolder.route
                    ) {
                        popUpTo(navHostController.graph.findStartDestination().id) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                },
                icon = painterResource(id = R.drawable.ic_new_folder),
                description = ""
            ),
            MiniFloatingAction(
                action = {
                    navController.navigate(
                        Screen.DetailsMessage.route
                    ) {
                        popUpTo(navHostController.graph.findStartDestination().id) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                },
                icon = painterResource(id = R.drawable.ic_new_message),
                description = ""
            ),
        ),
        iconCollapsed = painterResource(R.drawable.ic_arrow_left),
        iconExpanded = painterResource(R.drawable.ic_close),
    )
}