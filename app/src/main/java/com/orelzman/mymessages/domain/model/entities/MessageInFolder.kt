package com.orelzman.mymessages.domain.model.entities

import androidx.room.Entity

@Entity(primaryKeys = ["messageId", "folderId"])
data class MessageInFolder(
    val messageId: String = "",
    val folderId: String = "",
) : Loggable, Uploadable() {
    override val data: Map<String, Any>
        get() = mapOf(
            "message_id" to messageId,
            "folder_id" to folderId
        )
}