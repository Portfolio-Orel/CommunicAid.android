package com.orels.features.customer_status.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.SpaceEvenly
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.orels.features.R

/**
 * Created by Orel Zilberman on 30/03/2024.
 */
@Composable
fun CustomerStateScreen(
    viewModel: CustomerStateViewModel = hiltViewModel()
) {
    val state = viewModel.state
    Dialog(onDismissRequest = {}) {
        // Box to create a small square area
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = SpaceEvenly,
            modifier = Modifier
                .size(200.dp) // Square size
                .background(Color.White) // Background color of the square
        ) {
            DataRow(stringResource(R.string.full_name), state.name.toString())
            DataRow(stringResource(R.string.last_dive), state.lastDiveDate.toString())
            DataRow(
                stringResource(R.string.last_insurance_expiration),
                state.lastInsuranceExpirationDate.toString()
            )
            DataRow(stringResource(R.string.balance), state.balance.toString())
        }
    }
}

@Composable
fun DataRow(title: String, value: String) {
    // Direction rtl


    Row(
        horizontalArrangement = spacedBy(8.dp),
        ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Text(
                text = "$title:",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}