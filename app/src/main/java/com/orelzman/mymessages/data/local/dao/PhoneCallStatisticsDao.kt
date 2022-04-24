package com.orelzman.mymessages.data.local.dao

import androidx.room.*
import com.orelzman.mymessages.data.dto.PhoneCallStatistics

@Dao
interface PhoneCallStatisticsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(phoneCall: PhoneCallStatistics)

    @Update
    fun update(phoneCall: PhoneCallStatistics)

    @Delete
    fun delete(phoneCall: List<PhoneCallStatistics>)

    @Query("""
        SELECT *
        FROM PhoneCallStatistics
    """)
    fun getAll(): List<PhoneCallStatistics>
}