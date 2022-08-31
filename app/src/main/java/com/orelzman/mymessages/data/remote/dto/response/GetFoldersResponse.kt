package com.orelzman.mymessages.data.remote.dto.response

import com.google.gson.annotations.SerializedName
import com.orelzman.mymessages.domain.model.entities.Folder

data class GetFoldersResponse(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("times_used") val timesUsed: Int,
    @SerializedName("position") val position: Int,
)

val List<GetFoldersResponse>.folders: List<Folder>
    get() {
        val array = ArrayList<Folder>()
        forEach {
            with(it) {
                array.add(
                    Folder(
                        title = title,
                        isActive = true,
                        timesUsed = timesUsed,
                        position = position,
                        id = id
                    )
                )
            }
        }
        return array
    }