package com.orelzman.mymessages.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.orelzman.mymessages.domain.model.entities.DeletedCall
import kotlinx.coroutines.flow.Flow
import java.util.*

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
    fun getAll(startDate: Date): Flow<List<DeletedCall>>

    @Query(
        """
        SELECT *
        FROM DeletedCall
    """
    )
    fun getAllOnce(): List<DeletedCall>
}