package com.orelzman.mymessages.presentation.components.bottom_bar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.orelzman.mymessages.R
import com.orelzman.mymessages.domain.util.Screen

private val Screen.icon: ImageVector?
    get() =
        when (this) {
            Screen.UnhandledCalls -> Icons.Rounded.Phone
            Screen.Main -> Icons.Rounded.Home
            Screen.Statistics -> Icons.Rounded.Star
            else -> null
        }

private val Screen.label: Int
    get() =
        when (this) {
            Screen.UnhandledCalls -> R.string.unhandled_calls
            Screen.Main -> R.string.main
            Screen.Statistics -> R.string.statistics
            else -> 0
        }


@Composable
fun BottomBar(
    navController: NavHostController,
) {
    val bottomBarDestinations = listOf(Screen.UnhandledCalls, Screen.Statistics, Screen.Main)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
        bottomBarDestinations.forEach { screen ->
            NavigationBarItem(
                icon = {
                    screen.icon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = stringResource(screen.label)
                        )
                    }
                },
                label = {
                    Text(stringResource(screen.label))
                },
                alwaysShowLabel = false,
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = false
                        }
//                        launchSingleTop = true
                    }
                },
            )
        }
    }
}