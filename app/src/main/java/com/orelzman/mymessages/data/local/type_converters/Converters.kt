package com.orelzman.mymessages.data.local.type_converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orelzman.mymessages.data.dto.PhoneCall
import java.util.*

@ProvidedTypeConverter
class Converters {

    private val messageListType = object : TypeToken<List<String>?>() {}.type

    @TypeConverter
    fun stringToMessage(strings: String?): List<String> =
        Gson().fromJson(strings, messageListType)

    @TypeConverter
    fun messageToString(messages: List<String>?): String =
        Gson().toJson(messages)


    @TypeConverter
    fun dateToString(date: Date): String =
        Gson().toJson(date)

    @TypeConverter
    fun stringToDate(string: String): Date =
        Gson().fromJson(string, Date::class.java)

    @TypeConverter
    fun stringToPhoneCall(string: String): PhoneCall =
        Gson().fromJson(string, PhoneCall::class.java)

    @TypeConverter
    fun phoneCallToString(phoneCall: PhoneCall): String =
        Gson().toJson(phoneCall)

}