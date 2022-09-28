package com.orelzman.mymessages.data.local.type_converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orelzman.mymessages.domain.model.entities.MessageSent
import com.orels.domain.model.entities.SettingsKey
import com.orelzman.mymessages.domain.model.entities.StatisticsTypes
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
    fun dateToLong(date: Date): Long =
        date.time

    @TypeConverter
    fun longToDate(long: Long): Date =
        Date(long)

    @TypeConverter
    fun stringToSettingsKey(string: String): SettingsKey =
        Gson().fromJson(string, SettingsKey::class.java)

    @TypeConverter
    fun settingsKeysToString(settingsKeys: SettingsKey): String =
        Gson().toJson(settingsKeys)

    @TypeConverter
    fun uploadStateToString(uploadState: UploadState): String = uploadState.name

    @TypeConverter
    fun stringToUploadState(string: String): UploadState = UploadState.fromString(string)

    @TypeConverter
    fun statisticsTypeToString(statisticsType: StatisticsTypes): String =
        Gson().toJson(statisticsType)

    @TypeConverter
    fun stringToStatisticsType(string: String): StatisticsTypes =
        Gson().fromJson(string, StatisticsTypes::class.java)

    @TypeConverter
    fun anyToString(any: Any): String =
        Gson().toJson(any)

    @TypeConverter
    fun stringToAny(string: String): Any = Gson().fromJson(string, Any::class.java)
}