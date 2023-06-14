package com.orels.presentation.ui.components.bottom_bar

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.orels.domain.util.Screen
import com.orels.presentation.R


private val Screen.icon: Int?
    get() =
        when (this) {
            Screen.UnhandledCalls -> R.drawable.ic_missed_call
            Screen.Main -> R.drawable.ic_home
            Screen.Statistics -> R.drawable.ic_statistics
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
    val bottomBarDestinations = listOf(Screen.Statistics, Screen.Main)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
        bottomBarDestinations.forEach { screen ->
            NavigationBarItem(
                icon = {
                    screen.icon?.let {
                        Icon(
                            painter = painterResource(it),
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
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}