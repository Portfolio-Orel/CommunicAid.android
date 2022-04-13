package com.orelzman.mymessages.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MessageEntity(
    val title: String,
    val shortTitle: String,
    val body: String,
    @PrimaryKey val id: String
)