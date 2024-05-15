package com.orels.features.customer_status.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.hilt.navigation.compose.hiltViewModel
import com.orels.features.R
import com.orels.features.customer_status.domain.model.CustomerState
import com.orels.features.customer_status.domain.model.Finances
import com.orels.features.customer_status.domain.model.Insurance
import com.orels.features.customer_status.domain.model.LastDive
import com.orels.features.customer_status.domain.model.age
import com.orels.features.customer_status.domain.model.balanceFormatted
import com.orels.features.customer_status.domain.model.endFormatted
import com.orels.features.customer_status.domain.model.isNegative
import com.orels.features.customer_status.domain.model.isPositive
import com.orels.features.customer_status.domain.model.isValid
import com.orels.features.customer_status.domain.model.wasAtFormatted

/**
 * Created by Orel Zilberman on 30/03/2024.
 */
@Composable
fun CustomerStateScreen(
    onDismiss: () -> Unit = {},
    viewModel: CustomerStateViewModel = hiltViewModel()
) {
    val state = viewModel.state
//    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {

    Content(
        isLoading = state.isLoading,
        error = state.error,
        fullName = state.name.toString(),
        imageUri = state.image,
        lastDive = state.lastDive,
        insurance = state.insurance,
        finances = state.finances,
        customerState = state.customerState,
    ) {
        onDismiss()
    }
}

//}

@Composable
fun Content(
    isLoading: Boolean,
    fullName: String?,
    imageUri: String?,
    lastDive: LastDive?,
    insurance: Insurance?,
    finances: Finances?,
    customerState: CustomerState?,
    error: String? = null,
    onDismiss: () -> Unit
) {
    val goodModifier =
        Modifier
//            .background(Color.Green.copy(alpha = 0.3f), shape = RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 6.dp)
    val badModifier =
        Modifier
            .background(MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 6.dp)

    val lastDiveModifier = if (lastDive?.isValid() == true) goodModifier else badModifier
    val lastInsuranceExpirationModifier =
        if (insurance?.isValid() == true) goodModifier else badModifier
    val balanceModifier = if (finances?.isPositive() == true) goodModifier else badModifier


    Popup(
        onDismissRequest = { onDismiss() },
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            securePolicy = SecureFlagPolicy.SecureOff
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
//                .background(Color.Black.copy(alpha = 0.5f))
//                .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp))
                .clickable { onDismiss() }
        ) {
            Column(
                modifier = Modifier
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .fillMaxWidth(0.9f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(all = 16.dp)
                            .fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (fullName == null || fullName == "null") "" else fullName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp
                            ),
                            color = Color.White
                        )
                    }
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(24.dp)
                            .clickable { onDismiss() },
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(16.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                } else if (error != null) {
                    Text(
                        text = error,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                } else {
                    Column(
                        modifier = Modifier.padding(all = 16.dp),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        DataRow(
                            title = "ערך נכס",
                            value = finances?.revenue.toString(),
                            modifier = goodModifier,
                            isGood = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        DataRow(
                            stringResource(R.string.last_dive),
                            lastDive?.wasAtFormatted ?: stringResource(R.string.no_dive),
                            lastDiveModifier,
                            isGood = lastDive?.isValid() == true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        DataRow(
                            stringResource(R.string.last_insurance_expiration),
                            insurance?.endFormatted ?: stringResource(R.string.no_insurance),
                            lastInsuranceExpirationModifier,
                            isGood = insurance?.isValid() == true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        DataRow(
                            title = stringResource(R.string.age),
                            value = customerState?.age?.toString() ?: "0",
                            modifier = goodModifier,
                            isGood = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        DataRow(
                            stringResource(R.string.balance), finances?.balanceFormatted ?: "0",
                            modifier = balanceModifier,
                            isGood = finances?.isPositive() == true
                        )
                        if (finances?.isNegative() == true) {
                            Box(
                                modifier = Modifier
                                    .heightIn(max = 200.dp)
                                    .fillMaxWidth()
                                    .padding(4.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(
                                        12.dp,
                                        Alignment.CenterVertically
                                    )
                                ) {
                                    finances.outstandingAccounts?.forEach { accountEntry ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .shadow(
                                                    elevation = 2.dp,
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .background(color = Color.White)
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = accountEntry.value.balance,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Row {
                                                Text(
                                                    text = accountEntry.value.title,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = " •",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.Black
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DataRow(
    title: String, value: String,
    modifier: Modifier = Modifier,
    isGood: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(8.dp)
            )
            .background(color = Color.White),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.ExtraBold.takeIf { isGood }
                        ?: FontWeight.Normal,
                ),
                color = Color(16, 119, 16).takeIf { isGood } ?: MaterialTheme.colorScheme.error,
                modifier = modifier
            )
        }
    }
}