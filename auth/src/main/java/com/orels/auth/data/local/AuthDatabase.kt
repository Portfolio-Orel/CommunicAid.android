package com.orels.auth.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.orels.auth.data.local.dao.UserDao
import com.orels.auth.domain.model.User

@Database(
    entities = [
        User::class
    ],
    version = 2, exportSchema = false
)

abstract class AuthDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}