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
    """
    )
    fun getAll(): Flow<List<DeletedCall>>

    @Query(
        """
        SELECT *
        FROM DeletedCall
        WHERE DATE(deleteDate) > DATE(:startDate)
    """
    )
    fun getAllOnce(startDate: Long): List<DeletedCall>

    @Query("""
        SELECT COUNT(*)
        FROM DeletedCall
    """)
    fun getDBSize(): Int

    @Delete
    fun delete(deletedCall: DeletedCall)

    @Query("""
        DELETE
        FROM DeletedCall
    """)
    fun clear()
}