package com.orels.domain.model.dto.body.update

import com.google.gson.annotations.SerializedName

data class UpdateMessageBody(
    @SerializedName("id") val messageId: String,
    @SerializedName("title") val title: String,
    @SerializedName("short_title") val shortTitle: String,
    @SerializedName("body") val body: String,
    @SerializedName("times_used") val times_used: Int,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("position") val position: Int,
    @SerializedName("folder_id") val folderId: String? = null,
    @SerializedName("previous_folder_id") val previousFolderId: String? = null,
)