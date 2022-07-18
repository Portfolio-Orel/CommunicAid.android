package com.orelzman.mymessages.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalMaterialNavigationApi::class
)
@Composable
fun CustomScaffold(
    startRoute: String,
    navController: NavHostController,
    topBar: @Composable (String) -> Unit = {},
    bottomBar: @Composable (NavHostController) -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    content: @Composable (PaddingValues) -> Unit,
) {
//    val bottomSheetNavigator = rememberBottomSheetNavigator()

//    ModalBottomSheetLayout(
//        bottomSheetNavigator = bottomSheetNavigator,
//        sheetShape = RoundedCornerShape(16.dp)
//    ) {
        Scaffold(
            topBar = {  },
            bottomBar = { bottomBar(navController) },
            content = content,
            floatingActionButton = floatingActionButton,
            floatingActionButtonPosition = floatingActionButtonPosition
        )
//    }
}