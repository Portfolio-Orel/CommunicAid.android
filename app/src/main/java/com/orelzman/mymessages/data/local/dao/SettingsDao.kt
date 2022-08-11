package com.orelzman.mymessages.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.orelzman.mymessages.domain.model.entities.Settings
import com.orelzman.mymessages.domain.model.entities.SettingsKey
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(settings: Settings)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(settings: List<Settings>)

    @Query("""
        SELECT *
        FROM Settings
        WHERE `key` = :key
    """)
    fun get(key: SettingsKey): Settings?

    @Query("""
        SELECT *
        FROM Settings
    """)
    fun getAll(): List<Settings>

    @Query("""
        SELECT *
        FROM Settings
    """)
    fun getAllFlow(): Flow<List<Settings>>
}