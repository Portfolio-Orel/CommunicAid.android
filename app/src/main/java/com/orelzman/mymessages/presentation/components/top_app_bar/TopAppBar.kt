package com.orelzman.mymessages.presentation.components.top_app_bar

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.orelzman.mymessages.R

@Composable
fun TopAppBar(
    viewModel: TopAppBarViewModel = hiltViewModel()
) {
    val state = viewModel.state
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = if (state.callOnTheLine?.number == "" || state.callOnTheLine == null)
                    stringResource(R.string.no_active_call)
                else
                    state.callOnTheLine.getNameOrNumber(),
                style = MaterialTheme.typography.titleSmall,
            )
        },
        navigationIcon = {},
        actions = {},
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
        ),
        scrollBehavior = null
    )
}