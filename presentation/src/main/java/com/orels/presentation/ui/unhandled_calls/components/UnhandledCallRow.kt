package com.orels.presentation.ui.unhandled_calls.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.orels.domain.model.entities.PhoneCall
import com.orels.domain.util.extension.formatDayAndHours
import com.orels.presentation.R
import com.orels.presentation.theme.AppTheme
import com.orels.presentation.theme.MyMessagesTheme
import java.util.Date

private val RowShape = RoundedCornerShape(20.dp)

/**
 * A single unhandled call.
 *
 * The row is ALWAYS laid out like this, whatever the device's language direction:
 *
 *   `(photo) (name + time) ............ (delete) (dial)`
 *   left edge                                       right edge
 *
 * i.e. the dial and delete buttons are on the right (dial right-most) and the caller
 * identification is on the left. Only the dial button starts a call; the identification itself is
 * not clickable.
 */
@Composable
fun UnhandledCallRow(
    modifier: Modifier = Modifier,
    phoneCall: PhoneCall,
    photoUri: String = "",
    canDelete: Boolean = true,
    onDelete: (PhoneCall) -> Unit = { _ -> },
    onCall: (PhoneCall) -> Unit = { _ -> },
) {
    // Forced LTR so the physical positions above never flip. Text direction is still resolved
    // from the content, so Hebrew names/dates render correctly.
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        UnhandledCallRowContent(
            modifier = modifier,
            phoneCall = phoneCall,
            photoUri = photoUri,
            canDelete = canDelete,
            onDelete = onDelete,
            onCall = onCall,
        )
    }
}

@Composable
private fun UnhandledCallRowContent(
    modifier: Modifier,
    phoneCall: PhoneCall,
    photoUri: String,
    canDelete: Boolean,
    onDelete: (PhoneCall) -> Unit,
    onCall: (PhoneCall) -> Unit,
) {
    val context = LocalContext.current
    val nameOrNumber = phoneCall.getNameOrNumber()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RowShape,
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            )
            .clip(RowShape)
            .background(AppTheme.extended.cardSurface)
            .border(1.dp, AppTheme.extended.cardBorder, RowShape)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Caller photo – left
        CallerAvatar(name = nameOrNumber, photoUri = photoUri)

        // Caller identification – next to the photo
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = nameOrNumber,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = phoneCall.startDate.formatDayAndHours(context),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Buttons – right. Delete first, dial last so dial is the right-most.
        if (canDelete) {
            FilledTonalIconButton(
                onClick = { onDelete(phoneCall) },
                modifier = Modifier.size(44.dp),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_remove_circle_24),
                    contentDescription = stringResource(R.string.remove_unhandled_phone_call),
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        FilledTonalIconButton(
            onClick = { onCall(phoneCall) },
            modifier = Modifier.size(44.dp),
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Filled.Phone,
                contentDescription = stringResource(R.string.call_unhandled_phone_call),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

/**
 * Caller photo. Falls back to the first letter of the name, or to a generic person icon when the
 * caller is only a number (deliberately not a phone icon, which is the dial button's icon).
 * Not clickable.
 */
@Composable
private fun CallerAvatar(
    name: String,
    photoUri: String,
    modifier: Modifier = Modifier,
) {
    val firstChar = name.trim().firstOrNull()

    Box(
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        if (firstChar == null || firstChar.isDigit() || firstChar == '+') {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                modifier = Modifier.size(26.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        } else {
            Text(
                text = firstChar.uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        // Drawn on top of the fallback; stays invisible until (and unless) the photo loads
        if (photoUri.isNotBlank()) {
            AsyncImage(
                model = photoUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

private val previewCall = PhoneCall(
    number = "0543056286",
    name = "דנה כהן",
    startDate = Date(),
    endDate = Date(),
    actualEndDate = Date(),
    isWaiting = false,
    messagesSent = listOf()
)

@Preview(showBackground = true, backgroundColor = 0xFFF7F6FB)
@Composable
fun DefaultPreview2() {
    MyMessagesTheme {
        UnhandledCallRow(
            modifier = Modifier.padding(16.dp),
            phoneCall = previewCall
        )
    }
}

/** Must look exactly like the LTR preview: the row never mirrors. */
@Preview(showBackground = true, backgroundColor = 0xFFF7F6FB)
@Composable
fun UnhandledCallRowRtlPreview() {
    MyMessagesTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            UnhandledCallRow(
                modifier = Modifier.padding(16.dp),
                phoneCall = previewCall
            )
        }
    }
}
