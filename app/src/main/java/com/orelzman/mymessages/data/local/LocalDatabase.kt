package com.orelzman.mymessages.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.orelzman.mymessages.data.local.dao.MessagesDao
import com.orelzman.mymessages.data.local.entities.MessageEntity

@Database(
    entities = [MessageEntity::class],
    version = 1
)
abstract class LocalDatabase: RoomDatabase() {
    abstract val messageDao: MessagesDao
}