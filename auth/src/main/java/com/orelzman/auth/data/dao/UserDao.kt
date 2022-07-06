package com.orelzman.auth.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.orelzman.auth.domain.model.User

@Dao
interface UserDao {

    @Query("""
        SELECT *
        FROM User
    """)
    fun get(): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Query("""
        DELETE 
        FROM User
    """)
    fun clear()
}