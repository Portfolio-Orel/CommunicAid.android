package com.orelzman.mymessages.data.local.dao

import androidx.room.*
import com.orelzman.mymessages.data.dto.Folder

@Dao
interface FolderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(folder: Folder)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(folders: List<Folder>)

    @Query("""
        SELECT *
        FROM Folder
    """)
    suspend fun getFolders(): List<Folder>

    @Update
    suspend fun updateFolder(folder: Folder)

    @Query("""
        SELECT *
        FROM Folder
        WHERE id = :folderId
    """)
    suspend fun get(folderId: String): Folder

    @Query("DELETE FROM Folder")
    suspend fun clear()
}