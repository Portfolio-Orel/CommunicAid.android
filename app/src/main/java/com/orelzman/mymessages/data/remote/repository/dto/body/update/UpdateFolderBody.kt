package com.orelzman.mymessages.data.remote.repository.dto.body.update

import com.google.gson.annotations.SerializedName

data class UpdateFolderBody(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("times_used") val timesUsed: Int,
    @SerializedName("position") val position: Int,
)