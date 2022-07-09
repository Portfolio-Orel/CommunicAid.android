package com.orelzman.mymessages.data.local.dao

import androidx.room.*
import com.orelzman.mymessages.domain.model.entities.Folder
import kotlinx.coroutines.flow.Flow

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
    fun getFolders(): Flow<List<Folder>>

    @Query("""
        SELECT Count(*)
        FROM Folder
    """)
    suspend fun getFoldersCount(): Int

    @Update
    suspend fun update(folder: Folder)

    @Delete
    suspend fun delete(folder: Folder)

    @Query("""
        SELECT *
        FROM Folder
        WHERE id = :folderId
    """)
    suspend fun get(folderId: String): Folder

    @Query("DELETE FROM Folder")
    suspend fun clear()
}