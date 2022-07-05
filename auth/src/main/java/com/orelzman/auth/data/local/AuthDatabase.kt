package com.orelzman.auth.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.orelzman.auth.data.dao.UserDao
import com.orelzman.auth.domain.model.User

@Database(
    entities = [
        User::class
    ],
    version = 1
)

abstract class AuthDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}