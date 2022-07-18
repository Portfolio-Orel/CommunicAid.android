package com.orelzman.mymessages.presentation.components.bottom_bar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.orelzman.mymessages.R
import com.orelzman.mymessages.util.Screen

private val Screen.icon: ImageVector?
    get() =
        when (this) {
            Screen.UnhandledCalls -> Icons.Rounded.Phone
            Screen.Main -> Icons.Rounded.Home
            else -> null
        }

private val Screen.label: Int
    get() =
        when (this) {
            Screen.UnhandledCalls -> R.string.unhandled_calls
            Screen.Main -> R.string.main
            else -> 0
        }


@Composable
fun BottomBar(
    currentDestination: Screen,
    onBottomBarItemClick: (Screen) -> Unit
) {
    val bottomBarDestinations = listOf(Screen.UnhandledCalls, Screen.Main)

    NavigationBar {
        bottomBarDestinations.forEach { destination ->
            NavigationBarItem(
                icon = {
                    destination.icon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = stringResource(destination.label)
                        )
                    }
                },
                label = {
                    Text(stringResource(destination.label))
                },
                alwaysShowLabel = false,
                selected = currentDestination.route == destination.route,
                onClick = {
                    onBottomBarItemClick(destination)
                },
            )
        }
    }
}