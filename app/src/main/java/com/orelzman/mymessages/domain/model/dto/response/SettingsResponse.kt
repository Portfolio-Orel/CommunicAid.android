package com.orelzman.mymessages.domain.model.dto.response

import com.google.gson.annotations.SerializedName
import com.orelzman.mymessages.domain.model.entities.Settings
import com.orelzman.mymessages.domain.model.entities.SettingsKey

data class SettingsResponse(
    @SerializedName("key") val key: String,
    @SerializedName("value") val value: String,
    @SerializedName("enabled") val enabled: Boolean
)

fun List<SettingsResponse>.toSettings(): List<Settings> {
    val array = ArrayList(this)
    val settings = ArrayList<Settings>()
    array.forEach {settingsResponse ->
        SettingsKey.fromString(settingsResponse.key)?.let { settingsKey ->
            settings.add((Settings(key = settingsKey, value = settingsResponse.value, enabled = settingsResponse.enabled)))
        }
    }
    return settings
}