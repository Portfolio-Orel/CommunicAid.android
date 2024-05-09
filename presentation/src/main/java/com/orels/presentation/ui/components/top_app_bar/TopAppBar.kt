package com.orels.presentation.ui.components.top_app_bar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.orels.domain.util.Screen
import com.orels.features.customer_status.presentation.CustomerStateScreen
import com.orels.presentation.R
import com.orels.presentation.theme.noRippleClickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    navController: NavController,
    viewModel: TopAppBarViewModel = hiltViewModel()
) {
    val modifier = Modifier.padding(vertical = 2.dp)
    val state = viewModel.state
    var showCallerInfo by remember { mutableStateOf(false) }


    if (showCallerInfo) {
        CustomerStateScreen(
            onDismiss = { showCallerInfo = false }
        )
    }

    Column(modifier = modifier) {
        CenterAlignedTopAppBar(
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier.noRippleClickable {
                            showCallerInfo = true
                        },
                        text = if (state.callOnTheLine?.number == "" || state.callOnTheLine == null)
                            stringResource(R.string.no_active_call)
                        else
                            state.callOnTheLine.getNameOrNumber(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (state.callOnTheLine?.number == "" || state.callOnTheLine == null)
                            TextDecoration.None
                        else
                            TextDecoration.Underline
                    )
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
                        modifier = Modifier.size(24.dp),
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
        Divider()
    }
}