package com.orelzman.mymessages.data.remote.repository.dto.body.create

import com.google.gson.annotations.SerializedName
import com.orelzman.mymessages.data.dto.Message

data class CreateMessageBody(
    @SerializedName("title") val title: String,
    @SerializedName("short_title") val shortTitle: String,
    @SerializedName("body") val body: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("folder_id") val folderId: String,
    @SerializedName("position") val position: Int? = null,
) {
    companion object {
        fun fromMessage(userId: String, message: Message, folderId: String) =
            with(message) {
                CreateMessageBody(
                    title = title,
                    shortTitle = shortTitle,
                    body = body,
                    userId = userId,
                    folderId = folderId,
                    position = position
                )
            }
    }
}