package com.orelzman.mymessages.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.orelzman.mymessages.data.dto.Folder
import com.orelzman.mymessages.data.dto.Message
import com.orelzman.mymessages.data.local.dao.FolderDao
import com.orelzman.mymessages.data.local.dao.MessageDao
import com.orelzman.mymessages.data.local.type_converters.Converters

@Database(
    entities = [
        Message::class,
        Folder::class,
    ],
    version = 2
)
@TypeConverters(Converters::class)
abstract class LocalDatabase : RoomDatabase() {
    abstract val messageDao: MessageDao
    abstract val folderDao: FolderDao
}