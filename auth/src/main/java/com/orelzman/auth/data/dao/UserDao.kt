package com.orelzman.auth.data.dao

import androidx.room.*
import com.orelzman.auth.domain.model.User
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

    @Query(
        """
        SELECT *
        FROM User
    """
    )
    fun getUserFlow(): Flow<User?>

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