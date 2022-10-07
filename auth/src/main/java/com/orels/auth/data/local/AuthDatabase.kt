package com.orels.auth.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.orels.auth.data.dao.UserDao
import com.orels.auth.domain.model.User

@Database(
    entities = [
        User::class
    ],
    version = 3
)

abstract class AuthDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}