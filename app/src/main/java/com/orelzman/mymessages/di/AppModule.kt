package com.orelzman.mymessages.di

import android.app.Application
import androidx.room.Room
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.gson.Gson
import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.data.local.type_converters.Converters
import com.orelzman.mymessages.data.remote.BaseProjectUrl
import com.orelzman.mymessages.data.remote.EnvironmentRepository
import com.orelzman.mymessages.data.remote.Environments
import com.orelzman.mymessages.data.remote.repository.api.API
import com.orelzman.mymessages.domain.manager.PhoneCall.PhoneCallManagerImpl
import com.orelzman.mymessages.domain.service.phone_call.PhoneCallManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
    fun providePhoneCallManager(phoneCallManagerImpl: PhoneCallManagerImpl): PhoneCallManager = phoneCallManagerImpl

    @Provides
    @BaseProjectUrl
    fun provideBaseUrl(
        environmentRepository: EnvironmentRepository
    ) = when (environmentRepository.currentEnvironment) {
        Environments.Local -> "http://localhost:4000"
        Environments.Production -> "https://22jwmm93j9.execute-api.us-east-1.amazonaws.com"
    }

    @Provides
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient()

    @Provides
    fun provideGson(): Gson = Gson()

    @Provides
    fun providesAPI(
        okHttpClient: OkHttpClient,
        gson: Gson,
        @BaseProjectUrl url: String
    ): API =
        Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(url)
            .build()
            .create(API::class.java)
}