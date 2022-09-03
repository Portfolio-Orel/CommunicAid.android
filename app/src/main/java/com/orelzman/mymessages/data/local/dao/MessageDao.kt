package com.orelzman.mymessages.data.local.dao

import androidx.room.*
import com.orelzman.mymessages.domain.model.entities.Message
import kotlinx.coroutines.flow.Flow

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

    fun getMessages(): Flow<List<Message>>
    @Query("""
        SELECT *
        FROM Message
    """)
    fun getMessagesOnce(): List<Message>

    @Delete
    fun delete(message: Message)

    @Query("DELETE FROM Message")
    suspend fun clear()

    @Query("""
        SELECT * 
        FROM Message
        WHERE id = :messageId
    """)
    suspend fun getMessage(messageId: String): Message?

    @Update
    suspend fun update(message: Message)
}