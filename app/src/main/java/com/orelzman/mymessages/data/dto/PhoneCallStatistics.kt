package com.orelzman.mymessages.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PhoneCallStatistics(
    val phoneCall: PhoneCall,
    @PrimaryKey val id: String = "${phoneCall.startDate}",
    var isAddPhoneCall: Boolean = false,
    var isUpdateStatistics: Boolean = false,
)