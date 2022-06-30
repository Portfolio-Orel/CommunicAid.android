package com.orelzman.mymessages.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MessageInFolder(
    @PrimaryKey val id: String = "",
    val messageId: String = "",
    val folderId: String = "",
)