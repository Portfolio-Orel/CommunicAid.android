package com.orels.presentation.ui.components.bottom_bar

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    val isSmallScreen = screenHeight < 500

    val bottomBarDestinations =
        if (isSmallScreen) {
            listOf(Screen.Main, Screen.UnhandledCalls)
        } else {
            listOf(Screen.Main, Screen.Statistics, Screen.UnhandledCalls)
        }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = if (isSmallScreen) Modifier.height(40.dp) // 56dp is the default height of the bottom bar
        else Modifier
    ) {
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
                    Text(
                        text = if (isSmallScreen) "" else stringResource(screen.label),
                        style = MaterialTheme.typography.bodyMedium,
                    )
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