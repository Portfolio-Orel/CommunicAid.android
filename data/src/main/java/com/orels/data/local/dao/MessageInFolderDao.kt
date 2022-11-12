package com.orels.data.local.dao

import androidx.room.*
import com.orels.domain.model.entities.MessageInFolder
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageInFolderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(messageInFolder: MessageInFolder)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(messagesInFolder: List<MessageInFolder>)

    @Query(
        """
        SELECT *
        FROM MessageInFolder
    """
    )
    fun get(): Flow<List<MessageInFolder>>

    @Query(
        """
        SELECT *
        FROM MessageInFolder
    """
    )
    fun getOnce(): List<MessageInFolder>

    @Query("DELETE FROM MessageInFolder")
    fun clear()

    @Update
    fun update(messageInFolder: MessageInFolder)

    fun delete(folderId: String, isActive: Boolean = false) {
        setIsActive(folderId = folderId, isActive = isActive)
    }

    fun restore(folderId: String, isActive: Boolean = true) {
        setIsActive(folderId = folderId, isActive = isActive)
    }

    @Query(
        """
        UPDATE MessageInFolder
        SET isActive = 'false'
        WHERE folderId = :folderId AND messageId = :messageId
    """
    )
    fun delete(folderId: String, messageId: String)

    @Query(
        """
        SELECT folderId
        FROM MessageInFolder
        WHERE messageId = :messageId
    """
    )
    fun getWithMessageId(messageId: String): String?

    @Query(
        """
        SELECT *
        FROM MessageInFolder
        WHERE messageId = :messageId AND folderId = :folderId
    """
    )
    fun get(messageId: String, folderId: String): MessageInFolder

    @Query(
        """
            UPDATE MessageInFolder
            SET isActive = :isActive
            WHERE folderId = :folderId
        """
    )
    fun setIsActive(folderId: String, isActive: Boolean)
}