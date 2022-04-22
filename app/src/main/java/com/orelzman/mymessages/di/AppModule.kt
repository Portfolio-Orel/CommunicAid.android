package com.orelzman.mymessages.di

import android.app.Application
import androidx.room.Room
import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.data.local.type_converters.Converters
import com.orelzman.mymessages.domain.service.PhoneCall.PhoneCallInteractor
import com.orelzman.mymessages.domain.service.PhoneCall.PhoneCallInteractorImpl
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
            .build()
    @Provides
    @Singleton
    fun providePhoneCallInteractor(phoneCallInteractorImpl: PhoneCallInteractorImpl): PhoneCallInteractor = phoneCallInteractorImpl

}