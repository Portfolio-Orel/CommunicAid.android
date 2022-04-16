package com.orelzman.mymessages.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.orelzman.mymessages.data.dto.Folder

@Dao
interface FolderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: Folder)

    @Query("""
        SELECT *
        FROM Folder
    """)
    suspend fun getFolders(): List<Folder>

    @Query("DELETE FROM Folder")
    suspend fun clear()
}