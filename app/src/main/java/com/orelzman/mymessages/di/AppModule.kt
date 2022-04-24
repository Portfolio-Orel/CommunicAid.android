package com.orelzman.mymessages.di

import android.app.Application
import androidx.room.Room
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.data.local.type_converters.Converters
import com.orelzman.mymessages.domain.service.PhoneCall.PhoneCallManager
import com.orelzman.mymessages.domain.service.PhoneCall.PhoneCallManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideLocalDatabase(app: Application): LocalDatabase =
        Room.databaseBuilder(
            app,
            LocalDatabase::class.java,
            "mymessagesdb.db"
        )
            .addTypeConverter(Converters())
            .fallbackToDestructiveMigration()
            .build()

    @ExperimentalPermissionsApi
    @Provides
    @Singleton
    fun providePhoneCallManagr(phoneCallManagerImpl: PhoneCallManagerImpl): PhoneCallManager = phoneCallManagerImpl


}