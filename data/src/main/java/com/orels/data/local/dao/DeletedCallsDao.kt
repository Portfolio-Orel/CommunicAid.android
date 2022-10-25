package com.orels.data.local.dao

import androidx.room.*
import com.orels.domain.model.entities.DeletedCall
import kotlinx.coroutines.flow.Flow

@Dao
interface DeletedCallsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(deletedCalls: DeletedCall)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(deletedCalls: List<DeletedCall>)

    @Query(
        """
        SELECT *
        FROM DeletedCall
    """
    )
    fun getAll(): Flow<List<DeletedCall>>

    @Query(
        """
        SELECT *
        FROM DeletedCall
        WHERE deleteDate > :startDate
    """
    )
    fun getAllOnce(startDate: Long): List<DeletedCall>

    @Query(
        """
        SELECT *
        FROM DeletedCall
    """
    )
    fun getAllOnce(): List<DeletedCall>

    @Delete
    fun delete(deletedCall: DeletedCall)

    @Update
    fun update(deletedCall: DeletedCall)

    @Query(
        """
        DELETE
        FROM DeletedCall
    """
    )
    fun clear()
}