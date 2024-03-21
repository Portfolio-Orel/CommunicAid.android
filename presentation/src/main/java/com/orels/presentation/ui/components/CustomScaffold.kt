package com.orels.presentation.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavHostController
import com.orels.presentation.ui.components.util.SnackbarController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomScaffold(
    navController: NavHostController,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable (NavHostController) -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    content: @Composable (PaddingValues) -> Unit,
) {
    val configuration = LocalConfiguration.current

    val screenHeight = configuration.screenHeightDp

    if(screenHeight > 500) {
    Scaffold(
        topBar = topBar ,
        bottomBar = { bottomBar(navController) },
        content = content,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        snackbarHost = {
            SnackbarHost(hostState = SnackbarController.getInstance().snackbarHostState.value)
        }   
    )
    } else {
        Scaffold(
            topBar = topBar,
            content = content,
            snackbarHost = {
                SnackbarHost(hostState = SnackbarController.getInstance().snackbarHostState.value)
            }
        )
    }
}