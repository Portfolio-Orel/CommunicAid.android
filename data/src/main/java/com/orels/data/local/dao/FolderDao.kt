package com.orels.data.local.dao

import androidx.room.*
import com.orels.domain.model.entities.Folder
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(folder: Folder)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(folders: List<Folder>)

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
    fun getFoldersCount(): Int?

    @Update
    fun update(folder: Folder)

        @Query("""
            UPDATE Folder
            SET isActive = 0
            WHERE id = :id
        """)
    fun delete(id: String)

    @Query("""
        SELECT *
        FROM Folder
        WHERE id = :folderId
    """)
    fun get(folderId: String): Folder?

    @Query("DELETE FROM Folder")
    fun clear()
}