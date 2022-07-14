package com.orelzman.mymessages.presentation.bottom_bar

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.orelzman.mymessages.R
import com.orelzman.mymessages.presentation.destinations.DirectionDestination
import com.orelzman.mymessages.presentation.destinations.MainScreenDestination
import com.orelzman.mymessages.presentation.destinations.StatsScreenDestination

@Composable
fun BottomBar() {

}

enum class BottomBarItem(
    val direction: DirectionDestination,
    val icon: ImageVector,
    @StringRes val label: Int
) {
    Stats(StatsScreenDestination, Icons.Default.Phone, R.string.statistics),
    Main(MainScreenDestination, Icons.Default.Home, R.string.main)
}