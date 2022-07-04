package com.orelzman.mymessages.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MessageInFolder(
    @PrimaryKey val id: String = "",
    val messageId: String = "",
    val folderId: String = "",
) : Loggable {
    override val data: Map<String, Any>
        get() = mapOf(
            "id" to id,
            "message_id" to messageId,
            "folder_id" to folderId
        )
}