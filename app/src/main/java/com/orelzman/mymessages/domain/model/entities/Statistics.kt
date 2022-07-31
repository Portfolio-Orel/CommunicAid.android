package com.orelzman.mymessages.domain.model.entities

import androidx.annotation.StringRes
import androidx.room.Entity
import com.orelzman.mymessages.R

@Entity(primaryKeys = ["key", "value"])
data class Statistics(
    val key: StatisticsTypes,
    val value: Any
)

enum class StatisticsTypes(val value: String, @StringRes val label: Int) {
    IncomingCount("IncomingCount", R.string.incoming_calls),
    OutgoingCount("OutgoingCount", R.string.outgoing_calls),
    MissedCount("MissedCount", R.string.missed_calls),
    RejectedCalls("OutgoingCount", R.string.rejected_calls),
    MessagesCount("MessagesCount", R.string.messages_sent_count_by_type),
    Unknown("Unknown", R.string.unknown);
}