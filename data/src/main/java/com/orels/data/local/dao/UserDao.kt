package com.orels.data.local.dao

import androidx.room.*
import com.orels.domain.model.entities.User
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