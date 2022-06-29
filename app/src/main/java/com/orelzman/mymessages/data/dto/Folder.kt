package com.orelzman.mymessages.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Folder(
    val title: String = "",
    val isActive: Boolean = true,
    val timesUsed: Long = 0,
    val position: Int = 0,
    @PrimaryKey val id: String = "",
) {
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
