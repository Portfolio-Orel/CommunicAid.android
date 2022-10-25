package com.orels.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.orels.data.local.dao.UserDao
import com.orels.domain.model.entities.User

@Database(
    entities = [
        User::class
    ],
    version = 4, exportSchema = false
)

abstract class AuthDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}