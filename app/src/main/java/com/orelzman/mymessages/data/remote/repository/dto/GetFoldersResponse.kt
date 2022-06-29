package com.orelzman.mymessages.data.remote.repository.dto

import com.google.gson.annotations.SerializedName

data class GetFoldersResponse(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("times_used") val timesUsed: Int,
    @SerializedName("position") val position: Int,
    )