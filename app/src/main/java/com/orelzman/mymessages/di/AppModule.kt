package com.orelzman.mymessages.di

import android.app.Application
import androidx.room.Room
import com.google.gson.Gson
import com.orelzman.mymessages.R
import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.data.local.type_converters.Converters
import com.orelzman.mymessages.data.remote.BaseProjectUrl
import com.orelzman.mymessages.data.remote.EnvironmentRepository
import com.orelzman.mymessages.data.remote.Environments
import com.orelzman.mymessages.data.remote.repository.api.API
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
    fun provideLocalDatabase(context: Application): LocalDatabase =
        with(context) {
            Room.databaseBuilder(
                context,
                LocalDatabase::class.java,
                getString(R.string.local_db_name)
            )
                .addTypeConverter(Converters())
                .fallbackToDestructiveMigration()
                .build()
        }

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