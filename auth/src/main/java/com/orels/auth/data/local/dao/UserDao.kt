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
    suspend fun get(): User?

    @Query(
        """
        SELECT *
        FROM User
    """
    )
    fun getFlow(): Flow<User?>

    @Update
    suspend fun update(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User?)

    @Query(
        """
        DELETE 
        FROM User
    """
    )
    suspend fun clear()

    // A function that updates token field in user table
    @Query(
        """
        UPDATE User
        SET token = :token
    """
    )
    suspend fun updateToken(token: String?)
}