package com.orelzman.mymessages.data.remote.repository.dto

import com.google.gson.annotations.SerializedName
import com.orelzman.mymessages.data.dto.Message
import com.orelzman.mymessages.data.dto.MessageInFolder

data class GetMessagesResponse(
    @SerializedName("message_in_folder_id") val messageInFolderId: String,
    @SerializedName("folder_id") val folderId: String,
    @SerializedName("message_id") val messageId: String,
    @SerializedName("title") val title: String,
    @SerializedName("short_title") val shortTitle: String,
    @SerializedName("body") val body: String,
    @SerializedName("position") val position: Int,
    @SerializedName("times_used") val timesUsed: Int,
) {
    fun toMessage(): Message =
        Message(
            title = title,
            shortTitle = shortTitle,
            body = body,
            timesUsed = timesUsed,
            isActive = true,
            id = messageId
        )
}

fun List<GetMessagesResponse>.toMessagesInFolders(): List<MessageInFolder> {
    val array = ArrayList<MessageInFolder>()
    forEach {
        with(it) {
            array.add(
                MessageInFolder(
                    id = messageInFolderId,
                    messageId = messageId,
                    folderId = folderId
                )
            )
        }
    }
    return array
}