package com.orels.features.customer_state.presentation.customer_details

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Created by Orel Zilberman on 30/03/2024.
 */
@Composable
fun CustomerStateScreen(
    viewModel: CustomerStateViewModel = hiltViewModel()
) {
    val state = viewModel.state
}