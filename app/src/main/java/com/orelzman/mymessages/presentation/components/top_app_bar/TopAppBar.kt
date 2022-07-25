package com.orelzman.mymessages.presentation.components.top_app_bar

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
                modifier = Modifier
                    .padding(6.dp),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        navigationIcon = {},
        actions = {},
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background),
        scrollBehavior = null
    )
}