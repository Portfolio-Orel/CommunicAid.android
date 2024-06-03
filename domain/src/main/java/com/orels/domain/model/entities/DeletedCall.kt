package com.orels.domain.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.bson.types.ObjectId
import java.util.Date


/**
 * Calls that were deleted by the user and left unhandled.
 */
@Entity
data class DeletedCall(
    @PrimaryKey var id: String = ObjectId().toHexString(),
    var number: String = "",
    var deleteDate: Long = Date().time,
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
