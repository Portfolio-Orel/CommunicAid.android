package com.orels.data.local.dao

import androidx.room.*
import com.orels.domain.model.entities.Statistics
import kotlinx.coroutines.flow.Flow

@Dao
interface StatisticsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(statistics: Statistics)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(statistics: List<Statistics>)

    @Query("""
        SELECT *
        FROM Statistics
    """)
    fun getAll(): List<Statistics>

    @Query("""
        SELECT *
        FROM Statistics
    """)
    fun getAllFlow(): Flow<List<Statistics>>

    @Update
    fun update(statistics: Statistics)

    @Query("""
        DELETE
        FROM Statistics
    """)
    fun clear()
}