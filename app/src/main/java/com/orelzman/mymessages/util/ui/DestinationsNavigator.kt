package com.orelzman.mymessages.util.extension

import androidx.navigation.NavOptionsBuilder
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

class DefaultDestinationNavigator() : DestinationsNavigator {
    override fun clearBackStack(route: String): Boolean = true

    override fun navigate(
        route: String,
        onlyIfResumed: Boolean,
        builder: NavOptionsBuilder.() -> Unit
    ) = Unit

    override fun navigateUp(): Boolean = true

    override fun popBackStack(): Boolean = true

    override fun popBackStack(route: String, inclusive: Boolean, saveState: Boolean): Boolean = true

}