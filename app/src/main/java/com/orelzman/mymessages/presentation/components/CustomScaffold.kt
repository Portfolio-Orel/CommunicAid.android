package com.orelzman.mymessages.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.plusAssign
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.orelzman.mymessages.presentation.NavGraphs
import com.orelzman.mymessages.presentation.destinations.Destination
import com.orelzman.mymessages.presentation.startDestination

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalMaterialNavigationApi::class
)
@Composable
fun CustomScaffold(
    navController: NavHostController,
    topBar: @Composable (Destination) -> Unit = {},
    bottomBar: @Composable (Destination) -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    content: @Composable (PaddingValues) -> Unit = {},
) {
    val destination = navController.currentDestination ?: NavGraphs.root.startDestination
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    navController.navigatorProvider += bottomSheetNavigator

    ModalBottomSheetLayout(
        bottomSheetNavigator = bottomSheetNavigator,
        sheetShape = RoundedCornerShape(16.dp)
    ) {
        Scaffold(
            topBar = { topBar(destination as Destination) },
            bottomBar = { bottomBar(destination as Destination) },
            content = content,
            floatingActionButton = floatingActionButton,
            floatingActionButtonPosition = floatingActionButtonPosition
        )
    }
}