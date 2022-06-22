package com.orelzman.mymessages.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.orelzman.mymessages.data.dto.DeletedUnhandledCalls

@Dao
interface UnhandledCallsDao {

    @Insert
    suspend fun insert(deletedUnhandledCalls: DeletedUnhandledCalls)

    @Update
    suspend fun update(deletedUnhandledCalls: DeletedUnhandledCalls)

    @Query(
        """
        SELECT *
        FROM DeletedUnhandledCalls
    """
    )
    suspend fun getAll(): List<DeletedUnhandledCalls>
}