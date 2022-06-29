package com.orelzman.mymessages.data.remote.repository.dto

import com.google.gson.annotations.SerializedName

data class CreateMessageBody(
    @SerializedName("title") val title: String,
    @SerializedName("short_title") val shortTitle: String,
    @SerializedName("body") val body: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("folder_id") val folderId: String,
    @SerializedName("position") val position: Int? = null,
)