package com.orelzman.mymessages.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Folder(
    val folderTitle: String,
    val folderTimesUsed: Int,
    val isActive: Boolean,
    val messages: List<Message>,
    @PrimaryKey val id: String,
)