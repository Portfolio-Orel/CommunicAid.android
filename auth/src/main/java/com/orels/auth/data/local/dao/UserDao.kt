package com.orels.auth.data.local.dao

import androidx.room.*
import com.orels.auth.domain.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query(
        """
        SELECT *
        FROM User
    """
    )
    fun get(): User?

    @Update
    fun update(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User?)

    @Query(
        """
        DELETE 
        FROM User
    """
    )
    fun clear()
}