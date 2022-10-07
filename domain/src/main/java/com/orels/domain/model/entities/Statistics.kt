package com.orels.domain.model.entities

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.room.Entity
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.orels.domain.R
import com.orels.domain.util.extension.log
import java.util.*
import kotlin.reflect.KClass

/**
 * @param extraIdentifier is used to further distinguish different statistic types
 * that have the same name but the value is different.
 */
@Entity(primaryKeys = ["key", "extraIdentifier"])
data class Statistics(
    var key: StatisticsTypes,
    var value: Any,
    var extraIdentifier: String = "",
    var startDate: Date? = null,
    var endDate: Date? = null
) {
    /**
     * Casts value to it's actual value type.
     * @return value of type [T] or the default value of the casting fails.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getRealValue(): T? {
        return try {
            val gson = GsonBuilder()
                .setLenient()
                .create()
            val json = gson.toJson(value)
            val realValue = gson
                .fromJson(json, key.valueType.java) ?: key.defaultValue
            return realValue as? T
        } catch (e: JsonSyntaxException) {
            e.log()
            key.defaultValue as? T
        }
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Statistics) {
            key.title == other.key.title && extraIdentifier == other.extraIdentifier
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + value.hashCode()
        result = 31 * result + extraIdentifier.hashCode()
        return result
    }
}

enum class StatisticsTypes(
    val title: String,
    val valueType: KClass<*>,
    val defaultValue: String,
    @StringRes val label: Int,
    @DrawableRes val icon: Int? = null
) {
    IncomingCount(
        title = "IncomingCount",
        valueType = Int::class,
        defaultValue = "-1",
        label = R.string.incoming_calls
    ),
    OutgoingCount(
        title = "OutgoingCount",
        valueType = Int::class,
        defaultValue = "-1",
        label = R.string.outgoing_calls
    ),
    MissedCount(
        title = "MissedCount",
        valueType = Int::class,
        defaultValue = "-1",
        label = R.string.missed_calls
    ),
    RejectedCalls(
        title = "OutgoingCount",
        valueType = Int::class,
        defaultValue = "-1",
        label = R.string.rejected_calls
    ),
    MessagesCount(
        title = "MessagesCount",
        valueType = Map::class,
        defaultValue = emptyMap<String, String>().toString(),
        label = R.string.messages_sent_count_by_type
    ),
    Unknown(
        title = "Unknown",
        valueType = String::class,
        defaultValue = "Unknown Statistics",
        label = R.string.unknown
    );
}