package com.orelzman.mymessages.data.local.type_converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orelzman.mymessages.domain.model.entities.MessageSent
import com.orelzman.mymessages.domain.model.entities.SettingsKeys
import com.orelzman.mymessages.domain.model.entities.UploadState
import java.util.*

@ProvidedTypeConverter
class Converters {

    private val messagesSentListType = object : TypeToken<List<MessageSent>?>() {}.type

    @TypeConverter
    fun stringToMessagesSent(string: String?): List<MessageSent> =
        Gson().fromJson(string, messagesSentListType)

    @TypeConverter
    fun messageSentToString(messages: List<MessageSent>?): String =
        Gson().toJson(messages)


    @TypeConverter
    fun dateToString(date: Date): String =
        Gson().toJson(date)

    @TypeConverter
    fun stringToDate(string: String): Date =
        Gson().fromJson(string, Date::class.java)

    @TypeConverter
    fun stringToSettingsKey(string: String): SettingsKeys =
        Gson().fromJson(string, SettingsKeys::class.java)

    @TypeConverter
    fun settingsKeysToString(settingsKeys: SettingsKeys): String =
        Gson().toJson(settingsKeys)

    @TypeConverter
    fun uploadStateToString(uploadState: UploadState): String = uploadState.name

    @TypeConverter
    fun stringToUploadState(string: String): UploadState = UploadState.fromString(string)

}