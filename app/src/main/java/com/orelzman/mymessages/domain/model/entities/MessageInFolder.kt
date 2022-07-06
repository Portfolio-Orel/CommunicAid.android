package com.orelzman.mymessages.domain.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class MessageInFolder(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
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