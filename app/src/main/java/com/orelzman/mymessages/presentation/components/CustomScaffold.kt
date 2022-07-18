package com.orelzman.mymessages.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.plusAssign
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.orelzman.mymessages.presentation.NavGraphs
import com.orelzman.mymessages.presentation.destinations.Destination
import com.orelzman.mymessages.presentation.startDestination
import com.orelzman.mymessages.util.Screen
import com.ramcosta.composedestinations.spec.Route

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalMaterialNavigationApi::class
)
@Composable
fun CustomScaffold(
    startRoute: String,
    navController: NavController,
    topBar: @Composable (Screen) -> Unit = {},
    bottomBar: @Composable (Screen) -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    content: @Composable (PaddingValues) -> Unit = {},
) {
    val bottomSheetNavigator = rememberBottomSheetNavigator()

    ModalBottomSheetLayout(
        bottomSheetNavigator = bottomSheetNavigator,
        sheetShape = RoundedCornerShape(16.dp)
    ) {
        Scaffold(
            topBar = {  },
            bottomBar = { bottomBar(destination) },
            content = content,
            floatingActionButton = floatingActionButton,
            floatingActionButtonPosition = floatingActionButtonPosition
        )
    }
}