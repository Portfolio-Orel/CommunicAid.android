package com.orelzman.mymessages.data.local.dao

import androidx.room.*
import com.orelzman.mymessages.domain.model.entities.DeletedCall
import kotlinx.coroutines.flow.Flow

@Dao
interface DeletedCallsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(deletedCalls: DeletedCall)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(deletedCalls: List<DeletedCall>)

    @Query(
        """
        SELECT *
        FROM DeletedCall
        WHERE deleteDate > :startDate
    """
    )
    fun getAll(startDate: Long): Flow<List<DeletedCall>>

    @Query(
        """
        SELECT *
        FROM DeletedCall
        where deleteDate > :startDate
    """
    )
    fun getAllOnce(startDate: Long): List<DeletedCall>

    @Delete
    fun delete(deletedCall: DeletedCall)
}