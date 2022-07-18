package com.orelzman.mymessages.util.extension

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.orelzman.mymessages.util.Screen

//val State<NavBackStackEntry?>.currentScreen: Screen
//get() {
//    val currentDestination = value?.destination
//    currentDestination?.hierarchy?.any { it.route == screen.route }
//}