package com.orelzman.mymessages.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Message(
    val title: String = "",
    val shortTitle: String = "",
    val body: String = "",
    val timesUsed: Int = 0,
    val isActive: Boolean = true,
    val position: Int? = null,
    @PrimaryKey val id: String = ""
) {

    val data: Map<String, Any>
        get() = mapOf(
            "messageTitle" to title,
            "messageShortTitle" to shortTitle,
            "messageBody" to body,
            "messageTimesUsed" to timesUsed,
            "isActive" to isActive,
        )

    constructor(message: Message, id: String) : this(
        title = message.title,
        shortTitle = message.shortTitle,
        body = message.body,
        timesUsed = message.timesUsed,
        isActive = message.isActive,
        id = id
    )

    constructor(data: MutableMap<String, Any>?, id: String): this(
        title = data?.get("messageTitle") as String,
        shortTitle = data["messageShortTitle"] as String,
        body = data["messageBody"] as String,
        timesUsed = data["messageTimesUsed"] as Long,
        isActive = data["isActive"] as Boolean,
        id = id
    )

    companion object {
        val default = Message(
            title = "Title",
            shortTitle = "Short title",
            body = "Body",
            timesUsed = 4,
            isActive = true,
            id = "id"
        )
    }

}

val List<Map<String, Any>?>.messages: List<Message>
    get() {
        val messages = ArrayList<Message>()
        for (item in this) {
            messages.add(
                Message(
                    title = item?.get("messageTitle") as? String ?: "",
                    shortTitle = item?.get("messageShortTitle") as? String ?: "",
                    body = item?.get("messageBody") as? String ?: "",
                    timesUsed = item?.get("messageTimesUsed") as? Long ?: 0,
                    isActive = item?.get("isActive") as Boolean,
                    id = item["id"] as? String ?: "",
                )
            )
        }
        return messages
    }

fun List<Message>.getByIds(ids: List<String>): List<Message> =
    filter { ids.contains(it.id) }
