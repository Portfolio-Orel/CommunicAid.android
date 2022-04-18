package com.orelzman.mymessages.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Message(
    val messageTitle: String,
    val messageShortTitle: String,
    val messageBody: String,
    val messageTimesUsed: Int = 0,
    val isActive: Boolean = true,
    @PrimaryKey val id: String = ""
) {
    val data: Map<String, Any>
        get() = mapOf(
            "messageTitle" to messageTitle,
            "messageShortTitle" to messageShortTitle,
            "messageBody" to messageBody,
            "messageTimesUsed" to messageTimesUsed,
            "isActive" to isActive,
        )

    constructor(message: Message, id: String) : this(
        messageTitle = message.messageTitle,
        messageShortTitle = message.messageShortTitle,
        messageBody = message.messageBody,
        messageTimesUsed = message.messageTimesUsed,
        isActive = message.isActive,
        id = id
    )

}

val List<Map<String, Any>?>.messages: List<Message>
    get() {
        val messages = ArrayList<Message>()
        for (item in this) {
            messages.add(
                Message(
                    messageTitle = item?.get("messageTitle") as String,
                    messageShortTitle = item["messageShortTitle"] as String,
                    messageBody = item["messageBody"] as String,
                    messageTimesUsed = item["messageTimesUsed"] as Int,
                    isActive = item["isActive"] as Boolean,
                    item["id"] as String,
                )
            )
        }
        return messages
    }