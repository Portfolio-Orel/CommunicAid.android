package com.orelzman.mymessages.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.orelzman.mymessages.data.local.entities.MessageEntity

@Dao
interface MessagesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(messageEntity: MessageEntity)

    @Query("""
        SELECT *
        FROM MessageEntity
    """)
    suspend fun getMessages(): List<MessageEntity>

    @Query("DELETE FROM MessageEntity")
    suspend fun clearMessages()
}