package com.orelzman.mymessages.domain.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.orelzman.mymessages.util.CallType
import java.util.*

/**
 * Calls that were deleted by the user and left unhandled.
 */
@Entity
data class DeletedCalls(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val phoneCall: PhoneCall = PhoneCall(),
    val deleteDate: Date = Date()
) : Loggable {


    fun asCallLogEntity(): CallLogEntity =
        CallLogEntity(
            number = phoneCall.number,
            duration = phoneCall.startDate.time - phoneCall.endDate.time,
            name = phoneCall.name,
            dateMilliseconds = phoneCall.startDate.time,
            callLogType = CallType.fromString(phoneCall.type)
        )

    override val data: Map<String, Any>
        get() = mapOf(
            "id" to id,
            "phone_call" to phoneCall.data,
            "delete_date" to deleteDate
        )
}

val ArrayList<DeletedCalls>.numbers: List<String>
    get() = map { it.phoneCall.number }
