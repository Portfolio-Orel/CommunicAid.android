package com.orelzman.mymessages.data.local.type_converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orelzman.mymessages.data.dto.MessageSent
import com.orelzman.mymessages.data.dto.PhoneCall
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
    fun stringToPhoneCall(string: String): PhoneCall =
        Gson().fromJson(string, PhoneCall::class.java)

    @TypeConverter
    fun phoneCallToString(phoneCall: PhoneCall): String =
        Gson().toJson(phoneCall)
}