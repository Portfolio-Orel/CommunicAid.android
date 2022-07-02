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
    val position: Int = 0,
    @PrimaryKey val id: String = ""
) {

    constructor(message: Message, id: String) : this(
        title = message.title,
        shortTitle = message.shortTitle,
        body = message.body,
        timesUsed = message.timesUsed,
        isActive = message.isActive,
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

fun List<Message>.getByIds(ids: List<String>): List<Message> =
    filter { ids.contains(it.id) }
