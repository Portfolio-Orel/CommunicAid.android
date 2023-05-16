package com.orels.auth.data.local.dao

import androidx.room.*
import com.orels.auth.domain.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query(
        """
        SELECT *
        FROM User
        WHERE userid != ""
    """
    )
    fun get(): User?

    @Query(
        """
        SELECT *
        FROM User
    """
    )
    fun getFlow(): Flow<User>

    @Update
    suspend fun update(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    suspend fun updateFieldsNotNull(user: User) {
        val oldUser = get() ?: return
        val newUser = oldUser.copy(
            token = if(user.token.isNullOrEmpty()) oldUser.token else user.token,
            username = user.username ?: oldUser.username,
            email = user.email ?: oldUser.email,
            firstName = user.firstName ?: oldUser.firstName,
            lastName = user.lastName ?: oldUser.lastName,
        )
        update(newUser)
    }

    suspend fun deleteAndInsert(user: User) {
        clear()
        insert(user)
    }

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