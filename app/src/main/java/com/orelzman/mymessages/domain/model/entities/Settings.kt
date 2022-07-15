package com.orelzman.mymessages.domain.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Settings(
    @PrimaryKey val key: SettingsKeys,
    val value: String
)

enum class SettingsKeys(val keyInServer: String) {
    CallsUpdateAt("calls_update_at"),
    LoginLimitExceeded("login_limit_exceeded");

    companion object {
        fun fromString(value: String): SettingsKeys? =
            if (values().any { it.keyInServer == value }) {
                values().first { it.keyInServer == value }
            } else {
                null
            }

    }
}