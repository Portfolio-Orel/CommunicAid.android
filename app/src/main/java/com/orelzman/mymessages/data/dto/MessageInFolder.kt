package com.orelzman.mymessages.data.dto

import androidx.room.Entity

@Entity(primaryKeys = ["messageId", "folderId"])
data class MessageInFolder(
    val messageId: String = "",
    val folderId: String = "",
)