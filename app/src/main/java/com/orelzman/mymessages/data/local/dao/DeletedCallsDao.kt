package com.orelzman.mymessages.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.orelzman.mymessages.domain.model.entities.DeletedCalls
import kotlinx.coroutines.flow.Flow

@Dao
interface DeletedCallsDao {

    @Insert
    suspend fun insert(deletedCalls: DeletedCalls)
    @Query(
        """
        SELECT *
        FROM DeletedCalls
    """
    )
    fun getAll(): Flow<List<DeletedCalls>>
}