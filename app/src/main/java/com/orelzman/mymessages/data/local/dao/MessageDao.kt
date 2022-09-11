package com.orelzman.mymessages.data.local.dao

import androidx.room.*
import com.orelzman.mymessages.domain.model.entities.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(message: Message)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(messages: List<Message>)

    @Update()
    fun update(messages: List<Message>)

    @Query("""
        SELECT *
        FROM Message
        WHERE isActive = :isActive
    """)
    fun getMessages(isActive: Boolean): Flow<List<Message>>

    @Query("""
        SELECT *
        FROM Message
        WHERE isActive = :isActive
    """)
    fun getMessagesOnce(isActive: Boolean): List<Message>

    fun delete(message: Message) {
        updateIsActive(messageId = message.id, isActive = false)
    }

    fun restore(message: Message) {
        updateIsActive(messageId = message.id, isActive = true)
    }

    @Query("DELETE FROM Message")
    fun clear()

    @Query("""
        SELECT * 
        FROM Message
        WHERE id = :messageId
    """)
    fun getMessage(messageId: String): Message?

    @Update
    fun update(message: Message)

    @Query("""
        UPDATE Message
        SET isActive = :isActive
        WHERE id = :messageId
    """)
    fun updateIsActive(messageId: String, isActive: Boolean)
}