package com.orelzman.mymessages.domain.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.orelzman.mymessages.presentation.components.dropdown.model.DropdownItem

@Entity
data class Folder(
    val title: String = "",
    var isActive: Boolean = true,
    val timesUsed: Int = 0,
    val position: Int = 0,
    @PrimaryKey val id: String = "",
) : Loggable, DropdownItem, Uploadable() {

    override val data: Map<String, Any>
        get() = mapOf(
            "title" to title,
            "is_active" to isActive,
            "times_used" to timesUsed,
            "position" to position,
            "id" to id
        )

    /* Dropdown Item */
    override fun getIdentifier(): String = id

    override fun getValue(): String = title
    /* DropdownItem */

    /* equals */
    override fun equals(other: Any?): Boolean {
        return if (other is Folder) {
            id == other.id
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + isActive.hashCode()
        result = 31 * result + timesUsed
        result = 31 * result + position
        result = 31 * result + id.hashCode()
        return result
    }
    /* equals */

    constructor(folder: Folder, id: String) : this(
        title = folder.title,
        isActive = folder.isActive,
        timesUsed = folder.timesUsed,
        id = id
    )
}
