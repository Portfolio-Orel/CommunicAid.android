package com.orelzman.mymessages.data.remote.dto.body.create

import com.google.gson.annotations.SerializedName

data class CreateFolderBody(
    @SerializedName("title") val title: String,
    @SerializedName("position") val position: Int? = null,
)