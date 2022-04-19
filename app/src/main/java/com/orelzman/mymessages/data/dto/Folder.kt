package com.orelzman.mymessages.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Folder(
    val folderTitle: String,
    val messages: List<String> = emptyList(),
    val isActive: Boolean = true,
    val folderTimesUsed: Long = 0,
    @PrimaryKey val id: String = "",
) {

    constructor(folder: Folder, id: String) : this(
        folderTitle = folder.folderTitle,
        messages = folder.messages,
        isActive =  folder.isActive,
        folderTimesUsed = folder.folderTimesUsed,
        id = id
    )

    val data: Map<String, Any>
        get() =
            mapOf(
                "folderTitle" to folderTitle,
                "folderTimesUsed" to folderTimesUsed,
                "isActive" to isActive,
                "messageIDs" to messages
            )
}

@Suppress("UNCHECKED_CAST")
val List<Map<String, Any>?>.folders: List<Folder>
    get() {
        val folders = ArrayList<Folder>()
        for(item in this) {
            folders.add(
                Folder(
                    item?.get("folderTitle") as? String ?: "",
                    item?.get("messageIDs") as? List<String>  ?: emptyList(),
                    item?.get("isActive") as? Boolean ?: true,
                    item?.get("folderTimesUsed") as? Long ?: 0,
                    item?.get("id") as? String ?: "",
                )
            )
        }
        return folders
    }