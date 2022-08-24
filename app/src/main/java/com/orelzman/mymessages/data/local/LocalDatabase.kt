package com.orelzman.mymessages.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.orelzman.mymessages.data.local.dao.*
import com.orelzman.mymessages.data.local.type_converters.Converters
import com.orelzman.mymessages.domain.model.entities.*

@Database(
    entities = [
        Message::class,
        Folder::class,
        MessageInFolder::class,
        PhoneCall::class,
        DeletedCall::class,
        Settings::class,
        Statistics::class,
    ],
    version = 26
)
@TypeConverters(Converters::class)
abstract class LocalDatabase : RoomDatabase() {
    abstract val messageDao: MessageDao
    abstract val folderDao: FolderDao
    abstract val phoneCallDao: PhoneCallDao
    abstract val messageInFolderDao: MessageInFolderDao
    abstract val deletedCallsDao: DeletedCallsDao
    abstract val settingsDao: SettingsDao
    abstract val statisticsDao: StatisticsDao
}