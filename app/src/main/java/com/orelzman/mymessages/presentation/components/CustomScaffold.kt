package com.orelzman.mymessages.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.orelzman.mymessages.presentation.components.util.SnackbarController

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = { },
        bottomBar = { bottomBar(navController) },
        content = content,
//        floatingActionButton = floatingActionButton,
//        floatingActionButtonPosition = floatingActionButtonPosition,
        snackbarHost = {
            SnackbarHost(hostState = SnackbarController.getInstance().snackbarHostState.value)
        }
    )
//    }
}