package com.orels.domain.model.entities

import androidx.room.Entity

@Entity(primaryKeys = ["messageId", "folderId"])
data class MessageInFolder(
    var messageId: String = "",
    var folderId: String = "",
    var isActive: Boolean,
) : Loggable, Uploadable() {
    override val data: Map<String, Any>
        get() = mapOf(
            "message_id" to messageId,
            "folder_id" to folderId
        )
}