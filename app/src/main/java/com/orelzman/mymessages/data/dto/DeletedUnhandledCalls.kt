package com.orelzman.mymessages.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.orelzman.mymessages.domain.model.CallLogEntity
import java.util.*

/**
 * Calls that were deleted by the user and left unhandled.
 */
@Entity
data class DeletedUnhandledCalls(
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

    fun asCallLogEntity(): CallLogEntity =
        CallLogEntity(
            number = phoneCall.number,
            duration = phoneCall.startDate.time - phoneCall.endDate.time,
            name = phoneCall.name,
            dateMilliseconds = phoneCall.startDate.time,
            callLogType = phoneCall.callType
        )
}

val ArrayList<DeletedUnhandledCalls>.numbers: List<String>
    get() = map { it.phoneCall.number }
