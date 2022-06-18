package com.orelzman.mymessages.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Calls that were deleted by the user and left unhandled.
 */
@Entity
data class UnhandledCall(
    @PrimaryKey val id: String = "",
    val phoneCall: PhoneCall = PhoneCall(),
    val deleteDate: Date = Date()
): DTO {
    override val data: Map<String, Any>
        get() = mapOf(
            "id" to id,
            "phoneCall" to phoneCall.data,
            "date" to deleteDate.time
        )
}