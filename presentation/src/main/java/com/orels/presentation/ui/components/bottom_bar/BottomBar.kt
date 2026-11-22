package com.orels.presentation.ui.components.bottom_bar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.orels.domain.util.Screen
import com.orels.presentation.R
import kotlinx.coroutines.launch
import androidx.compose.foundation.background

private val Screen.iconRes: Int?
    get() = when (this) {
        Screen.Main -> R.drawable.ic_home
        Screen.Statistics -> R.drawable.ic_statistics
        else -> null
    }

private val Screen.labelRes: Int
    get() = when (this) {
        Screen.UnhandledCalls -> R.string.unhandled_calls
        Screen.Main -> R.string.main
        Screen.Statistics -> R.string.statistics
        Screen.Settings -> R.string.settings
        else -> 0
    }

private val drawerDestinations = listOf(
    Screen.Main,
    Screen.Statistics,
    Screen.Settings,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomBar(
    navController: NavHostController,
    drawerState: DrawerState,
) {
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    ModalDrawerSheet(
        drawerShape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp)) {
            Spacer(Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "M",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp)
            )

            drawerDestinations.forEach { screen ->
                NavigationDrawerItem(
                    modifier = Modifier.padding(vertical = 2.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    icon = {
                        val iconRes = screen.iconRes
                        if (iconRes != null) {
                            Icon(
                                modifier = Modifier.size(22.dp),
                                painter = painterResource(iconRes),
                                contentDescription = stringResource(screen.labelRes)
                            )
                        } else {
                            Icon(
                                modifier = Modifier.size(22.dp),
                                imageVector = Icons.Rounded.Settings,
                                contentDescription = stringResource(R.string.settings)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = stringResource(screen.labelRes),
                            style = MaterialTheme.typography.titleSmall,
                        )
                    },
                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) { inclusive = false }
                            launchSingleTop = true
                        }
                        scope.launch { drawerState.close() }
                    },
                )
            }
        }
    }
}
