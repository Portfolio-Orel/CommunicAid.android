package com.orelzman.mymessages.domain.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Calls that were deleted by the user and left unhandled.
 */
@Entity
data class DeletedCall(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    val number: String = "",
    val deleteDate: Long = Date().time,
) : Loggable, Uploadable() {

    override val data: Map<String, Any>
        get() = mapOf(
            "id" to id,
            "number" to number,
            "delete_date" to deleteDate
        )
}

val ArrayList<DeletedCall>.numbers: List<String>
    get() = map { it.number }
