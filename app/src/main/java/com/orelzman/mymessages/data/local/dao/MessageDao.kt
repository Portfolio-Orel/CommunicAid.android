package com.orelzman.mymessages.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.orelzman.mymessages.data.dto.Message

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)

    @Query("""
        SELECT *
        FROM Message
    """)
    suspend fun getMessages(): List<Message>

    @Query("DELETE FROM Message")
    suspend fun clear()
}