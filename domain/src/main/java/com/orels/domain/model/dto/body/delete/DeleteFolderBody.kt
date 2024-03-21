package com.orels.domain.model.dto.body.delete

import com.google.gson.annotations.SerializedName

data class DeleteFolderBody(
    @SerializedName("folder_id") val folderId: String,
)