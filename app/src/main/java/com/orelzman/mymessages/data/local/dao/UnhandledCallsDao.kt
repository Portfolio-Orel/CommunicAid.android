package com.orelzman.mymessages.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.orelzman.mymessages.domain.model.entities.DeletedCalls

@Dao
interface UnhandledCallsDao {

    @Insert
    suspend fun insert(deletedCalls: DeletedCalls)

    @Update
    suspend fun update(deletedCalls: DeletedCalls)

    @Query(
        """
        SELECT *
        FROM DeletedCalls
    """
    )
    suspend fun getAll(): List<DeletedCalls>
}