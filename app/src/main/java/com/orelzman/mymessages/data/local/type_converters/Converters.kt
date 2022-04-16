package com.orelzman.mymessages.data.local.type_converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orelzman.mymessages.data.dto.Message

@ProvidedTypeConverter
class Converters {

    private val messageListType = object : TypeToken<List<Message>?>() {}.type
    @TypeConverter
    fun stringToMessage(strings: String?): List<Message> =
        Gson().fromJson(strings, messageListType)

    @TypeConverter
    fun messageToString(messages: List<Message>?): String =
        Gson().toJson(messages)


}