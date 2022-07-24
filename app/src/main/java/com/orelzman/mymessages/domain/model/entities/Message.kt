package com.orelzman.mymessages.domain.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Message(
    val title: String = "",
    val shortTitle: String = "",
    val body: String = "",
    var timesUsed: Int = 0,
    val isActive: Boolean = true,
    var position: Int = 0,
    @PrimaryKey val id: String = "",
) : Loggable, Uploadable() {

    constructor(message: Message, id: String) : this(
        title = message.title,
        shortTitle = message.shortTitle,
        body = message.body,
        timesUsed = message.timesUsed,
        isActive = message.isActive,
        id = id
    )

    override val data: Map<String, Any>
        get() = mapOf(
            "title" to title,
            "short_title" to shortTitle,
            "body" to body,
            "times_used" to timesUsed,
            "is_active" to isActive,
            "position" to position,
            "id" to id
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