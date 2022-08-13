package com.orelzman.mymessages.domain.model.entities

import androidx.annotation.StringRes
import androidx.room.Entity
import com.orelzman.mymessages.R

/**
 * @param extraIdentifier is used to further distinguish different statistic types
 * that have the same name but the value is different.
 */
@Entity(primaryKeys = ["key", "extraIdentifier"])
data class Statistics(
    val key: StatisticsTypes,
    val value: Any,
    val extraIdentifier: String = ""
) {
    override fun equals(other: Any?): Boolean {
        return if(other is Statistics) {
            key.value == other.key.value && extraIdentifier == other.extraIdentifier
        } else {
            false
        }
    }
}

enum class StatisticsTypes(val value: String, @StringRes val label: Int) {
    IncomingCount("IncomingCount", R.string.incoming_calls),
    OutgoingCount("OutgoingCount", R.string.outgoing_calls),
    MissedCount("MissedCount", R.string.missed_calls),
    RejectedCalls("OutgoingCount", R.string.rejected_calls),
    MessagesCount("MessagesCount", R.string.messages_sent_count_by_type),
    Unknown("Unknown", R.string.unknown);
}