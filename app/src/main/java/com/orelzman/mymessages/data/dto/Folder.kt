package com.orelzman.mymessages.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Folder(
    val title: String = "",
    val isActive: Boolean = true,
    val timesUsed: Int = 0,
    val position: Int = 0,
    @PrimaryKey val id: String = "",
) : Loggable {
    override val data: Map<String, Any>
        get() = mapOf(
            "title" to title,
            "is_active" to isActive,
            "times_used" to timesUsed,
            "position" to position,
            "id" to id
        )

    override fun equals(other: Any?): Boolean {
        return if (other is Folder) {
            id == other.id
        } else {
            false
        }
    }

    constructor(folder: Folder, id: String) : this(
        title = folder.title,
        isActive = folder.isActive,
        timesUsed = folder.timesUsed,
        id = id
    )
}
