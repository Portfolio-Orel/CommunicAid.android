package com.orelzman.mymessages.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Folder(
    val folderTitle: String = "",
    var messageIds: List<String> = emptyList(),
    val isActive: Boolean = true,
    val folderTimesUsed: Long = 0,
    @PrimaryKey val id: String = "",
) : DTO {

    override fun equals(other: Any?): Boolean {
        return if (other is Folder) {
            id == other.id
        } else {
            false
        }
    }

    override val data: Map<String, Any>
        get() =
            mapOf(
                "folderTitle" to folderTitle,
                "folderTimesUsed" to folderTimesUsed,
                "isActive" to isActive,
                "messageIDs" to messageIds
            )

    constructor(folder: Folder, id: String) : this(
        folderTitle = folder.folderTitle,
        messageIds = folder.messageIds,
        isActive = folder.isActive,
        folderTimesUsed = folder.folderTimesUsed,
        id = id
    )

    @Suppress("UNCHECKED_CAST")
    constructor(data: MutableMap<String, Any>?, id: String) : this(
        folderTitle = data?.get("folderTitle") as String,
        messageIds = data["messageIDs"] as? List<String> ?: emptyList(),
        isActive = data["isActive"] as Boolean,
        folderTimesUsed = data["folderTimesUsed"] as Long,
        id = id
    )

}

@Suppress("UNCHECKED_CAST")
val List<Map<String, Any>?>.folders: List<Folder>
    get() {
        val folders = ArrayList<Folder>()
        for (item in this) {
            folders.add(
                Folder(
                    item?.get("folderTitle") as? String ?: "",
                    item?.get("messageIDs") as? List<String> ?: emptyList(),
                    item?.get("isActive") as? Boolean ?: true,
                    item?.get("folderTimesUsed") as? Long ?: 0,
                    item?.get("id") as? String ?: "",
                )
            )
        }
        return folders
    }