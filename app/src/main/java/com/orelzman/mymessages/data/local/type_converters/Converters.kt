package com.orelzman.mymessages.data.local.type_converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@ProvidedTypeConverter
class Converters {

    private val messageListType = object : TypeToken<List<String>?>() {}.type
    @TypeConverter
    fun stringToMessage(strings: String?): List<String> =
        Gson().fromJson(strings, messageListType)

    @TypeConverter
    fun messageToString(messages: List<String>?): String =
        Gson().toJson(messages)


}