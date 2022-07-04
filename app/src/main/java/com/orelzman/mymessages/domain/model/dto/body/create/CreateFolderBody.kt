package com.orelzman.mymessages.domain.model.dto.body.create

import com.google.gson.annotations.SerializedName

data class CreateFolderBody(
    @SerializedName("title") val title: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("position") val position: Int? = null,
)