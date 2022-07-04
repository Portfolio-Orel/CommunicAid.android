package com.orelzman.mymessages.data.local.dao

import androidx.room.*
import com.orelzman.mymessages.domain.model.entities.Message

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: Message)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(messages: List<Message>)

    @Query("""
        SELECT *
        FROM Message
    """)
    suspend fun getMessages(): List<Message>

    @Query("DELETE FROM Message")
    suspend fun clear()

    @Query("""
        SELECT * 
        FROM Message
        WHERE id = :messageId
    """)
    suspend fun getMessage(messageId: String): Message

    @Update
    suspend fun update(message: Message)
}