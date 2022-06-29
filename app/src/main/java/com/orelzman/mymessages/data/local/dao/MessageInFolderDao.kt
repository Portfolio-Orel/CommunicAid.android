package com.orelzman.mymessages.data.local.dao

import androidx.room.*
import com.orelzman.mymessages.data.dto.MessageInFolder

@Dao
interface MessageInFolderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(messageInFolder: MessageInFolder)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(messagesInFolder: List<MessageInFolder>)

    @Query("""
        SELECT *
        FROM MessageInFolder
    """)
    suspend fun getMessageInFolders(): List<MessageInFolder>

    @Update
    suspend fun update(messageInFolder: MessageInFolder)

    @Delete
    suspend fun delete(messageInFolder: MessageInFolder)

    @Query("""
        SELECT *
        FROM MessageInFolder
        WHERE id = :messageInFolderId
    """)
    suspend fun get(messageInFolderId: String): MessageInFolder

    @Query("DELETE FROM MessageInFolder")
    suspend fun clear()
}