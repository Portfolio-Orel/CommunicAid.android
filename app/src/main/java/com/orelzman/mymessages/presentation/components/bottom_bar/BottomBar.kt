package com.orelzman.mymessages.presentation.components.bottom_bar

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.orelzman.mymessages.R
import com.orelzman.mymessages.presentation.destinations.Destination
import com.orelzman.mymessages.presentation.destinations.MainScreenDestination
import com.orelzman.mymessages.presentation.destinations.StatsScreenDestination
import com.orelzman.mymessages.presentation.destinations.UnhandledCallsScreenDestination
import com.ramcosta.composedestinations.spec.Direction
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

@OptIn(ExperimentalFoundationApi::class)
enum class BottomBarDestination(
    val direction: DirectionDestinationSpec,
    val icon: ImageVector,
    @StringRes val label: Int
) {
    UnhandledCalls(UnhandledCallsScreenDestination, Icons.Default.Phone, R.string.unhandled_calls),
    Home(MainScreenDestination, Icons.Default.Home, R.string.main),
}

@Composable
fun BottomBar(
    currentDestination: Destination,
    onBottomBarItemClick: (Direction) -> Unit
) {
    NavigationBar {
        BottomBarDestination.values().forEach { destination ->
            NavigationBarItem(
                icon = {
                    Icon(destination.icon, contentDescription = stringResource(destination.label))
                },
                label = {
                    Text(stringResource(destination.label))
                },
                alwaysShowLabel = false,
                selected = currentDestination == destination.direction,
                onClick = {
                    onBottomBarItemClick(destination.direction)
                },
            )
        }
    }
}