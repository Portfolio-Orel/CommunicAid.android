package com.orelzman.mymessages.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class GetMessagesResponse(
    @SerializedName("message_in_folder_id") val messageInFolderId: String,
    @SerializedName("folder_id") val folderId: String,
    @SerializedName("message_id") val messageId: String,
    @SerializedName("title") val title: String,
    @SerializedName("short_title") val shortTitle: String,
    @SerializedName("body") val body: String,
    @SerializedName("position") val position: Int,
    @SerializedName("times_used") val timesUsed: Int,
    @SerializedName("is_active") val isActive: Boolean

)