package com.orelzman.mymessages.domain.model.dto.body.create

import com.google.gson.annotations.SerializedName
import com.orelzman.mymessages.domain.model.entities.Message

data class CreateMessageBody(
    @SerializedName("title") val title: String,
    @SerializedName("short_title") val shortTitle: String,
    @SerializedName("body") val body: String,
    @SerializedName("folder_id") val folderId: String,
    @SerializedName("position") val position: Int? = null,
) {
    companion object {
        fun fromMessage(message: Message, folderId: String) =
            with(message) {
                CreateMessageBody(
                    title = title,
                    shortTitle = shortTitle,
                    body = body,
                    folderId = folderId,
                    position = position
                )
            }
    }
}