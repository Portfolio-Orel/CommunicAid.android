package com.orelzman.mymessages.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.orelzman.mymessages.data.dto.*
import com.orelzman.mymessages.data.local.dao.*
import com.orelzman.mymessages.data.local.type_converters.Converters

@Database(
    entities = [
        Message::class,
        Folder::class,
        MessageInFolder::class,
        PhoneCall::class,
        DeletedUnhandledCalls::class
    ],
    version = 4
)
@TypeConverters(Converters::class)
abstract class LocalDatabase : RoomDatabase() {
    abstract val messageDao: MessageDao
    abstract val folderDao: FolderDao
    abstract val phoneCallDao: PhoneCallDao
    abstract val messageInFolderDao: MessageInFolderDao
    abstract val unhandledCallDao: UnhandledCallsDao
}