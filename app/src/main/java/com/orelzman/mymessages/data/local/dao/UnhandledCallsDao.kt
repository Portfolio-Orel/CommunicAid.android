package com.orelzman.mymessages.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.orelzman.mymessages.data.dto.UnhandledCall

@Dao
interface UnhandledCallsDao {

    @Insert
    suspend fun insert(unhandledCall: UnhandledCall)

    @Update
    suspend fun update(unhandledCall: UnhandledCall)

    @Query("""
        SELECT *
        FROM UnhandledCall
    """)
    suspend fun getAll(): List<UnhandledCall>
}