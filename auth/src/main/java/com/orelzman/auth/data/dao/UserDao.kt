package com.orelzman.auth.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.orelzman.auth.domain.model.User

@Dao
interface UserDao {

    @Query("""
        SELECT *
        FROM User
    """)
    fun get(): User?

    @Insert
    fun insert(user: User)

    @Query("""
        DELETE 
        FROM User
    """)
    fun clear()
}