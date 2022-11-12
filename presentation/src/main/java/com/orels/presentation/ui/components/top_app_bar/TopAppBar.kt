package com.orels.presentation.ui.components.top_app_bar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.orels.domain.util.Screen
import com.orels.presentation.R

@Composable
fun TopAppBar(
    navController: NavController,
    viewModel: TopAppBarViewModel = hiltViewModel()
) {
    val state = viewModel.state
    CenterAlignedTopAppBar(
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (state.callOnTheLine?.number == "" || state.callOnTheLine == null)
                        stringResource(R.string.no_active_call)
                    else
                        state.callOnTheLine.getNameOrNumber(),
                    style = MaterialTheme.typography.titleSmall,
                )
//                if (BuildConfig.DEBUG) {
//                    Text(
//                        text = "Debug_new",
//                        style = MaterialTheme.typography.bodySmall,
//                        color = MaterialTheme.colorScheme.error
//                    )
//                }
            }
        },
        navigationIcon = {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .size(32.dp)
                    .clickable {
                        navController.navigate(Screen.Settings.route)
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = stringResource(R.string.icon_settings),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        actions = {},
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
        ),
        scrollBehavior = null
    )
}