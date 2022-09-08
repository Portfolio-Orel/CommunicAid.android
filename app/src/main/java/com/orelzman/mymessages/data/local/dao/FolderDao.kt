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
        Where isActive = :isActive
    """)
    fun getFolders(isActive: Boolean): Flow<List<Folder>>

    @Query("""
        SELECT *
        FROM Folder
        Where isActive = :isActive
    """)
    fun getFoldersOnce(isActive: Boolean): List<Folder>


    @Query("""
        SELECT Count(*)
        FROM Folder
    """)
    suspend fun getFoldersCount(): Int

    @Update
    suspend fun update(folder: Folder)

        @Query("""
            UPDATE Folder
            SET isActive = 0
            WHERE id = :id
        """)
    suspend fun delete(id: String)

    @Query("""
        SELECT *
        FROM Folder
        WHERE id = :folderId
    """)
    fun get(folderId: String): Folder

    @Query("DELETE FROM Folder")
    suspend fun clear()
}