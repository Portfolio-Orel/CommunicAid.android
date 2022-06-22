package com.orelzman.mymessages.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.orelzman.mymessages.data.dto.Folder
import com.orelzman.mymessages.data.dto.Message
import com.orelzman.mymessages.data.dto.PhoneCallStatistics
import com.orelzman.mymessages.data.dto.DeletedUnhandledCalls
import com.orelzman.mymessages.data.local.dao.FolderDao
import com.orelzman.mymessages.data.local.dao.MessageDao
import com.orelzman.mymessages.data.local.dao.PhoneCallStatisticsDao
import com.orelzman.mymessages.data.local.dao.UnhandledCallsDao
import com.orelzman.mymessages.data.local.type_converters.Converters

@Database(
    entities = [
        Message::class,
        Folder::class,
        PhoneCallStatistics::class,
        DeletedUnhandledCalls::class,
    ],
    version = 3
)
@TypeConverters(Converters::class)
abstract class LocalDatabase : RoomDatabase() {
    abstract val messageDao: MessageDao
    abstract val folderDao: FolderDao
    abstract val phoneCallDao: PhoneCallStatisticsDao
    abstract val unhandledCallDao: UnhandledCallsDao
}