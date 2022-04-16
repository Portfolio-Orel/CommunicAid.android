package com.orelzman.mymessages.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Message(
    val messageTitle: String,
    val messageShortTitle: String,
    val messageBody: String,
    val messageTimesUsed: Int,
    val isActive: Boolean,
    @PrimaryKey val id: String
)