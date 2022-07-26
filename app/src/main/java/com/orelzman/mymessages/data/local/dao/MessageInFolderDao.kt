package com.orelzman.mymessages.data.local.dao

import androidx.room.*
import com.orelzman.mymessages.domain.model.entities.MessageInFolder
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageInFolderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(messageInFolder: MessageInFolder)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(messagesInFolder: List<MessageInFolder>)

    @Query(
        """
        SELECT *
        FROM MessageInFolder
    """
    )
    fun get(): Flow<List<MessageInFolder>>

    @Update
    suspend fun update(messageInFolder: MessageInFolder)

    @Delete
    suspend fun delete(messageInFolder: MessageInFolder)

    @Query("""
        DELETE 
        FROM MessageInFolder
        WHERE folderId = :folderId
    """)
    suspend fun delete(folderId: String)

    @Query(
        """
        SELECT folderId
        FROM MessageInFolder
        WHERE messageId = :messageId
    """
    )
    suspend fun getWithMessageId(messageId: String): String?

    @Query(
        """
        SELECT *
        FROM MessageInFolder
        WHERE messageId = :messageId AND folderId = :folderId
    """
    )
    suspend fun get(messageId: String, folderId: String): MessageInFolder

    @Query("DELETE FROM MessageInFolder")
    suspend fun clear()
}