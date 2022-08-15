package com.orelzman.mymessages.domain.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.orelzman.mymessages.domain.util.extension.withoutPrefix
import java.util.*

/**
 * Calls that were deleted by the user and left unhandled.
 */
@Entity
data class DeletedCall(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    var number: String = "",
    val deleteDate: Long = Date().time,
) : Loggable, Uploadable() {

    init {
        number = number.withoutPrefix()
    }

    override val data: Map<String, Any>
        get() = mapOf(
            "id" to id,
            "number" to number,
            "delete_date" to deleteDate
        )
}

val ArrayList<DeletedCall>.numbers: List<String>
    get() = map { it.number }
