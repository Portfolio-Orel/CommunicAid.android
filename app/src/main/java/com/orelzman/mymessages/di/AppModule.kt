package com.orelzman.mymessages.di

import android.app.Application
import androidx.annotation.RawRes
import androidx.room.Room
import com.google.gson.Gson
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.R
import com.orelzman.mymessages.data.interceptor.AuthInterceptor
import com.orelzman.mymessages.data.interceptor.ErrorInterceptor
import com.orelzman.mymessages.data.interceptor.LogInterceptor
import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.data.local.type_converters.Converters
import com.orelzman.mymessages.data.remote.EnvironmentRepository
import com.orelzman.mymessages.data.remote.Environments.*
import com.orelzman.mymessages.data.remote.repository.api.API
import com.orelzman.mymessages.domain.AuthConfigFile
import com.orelzman.mymessages.domain.BaseProjectUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideLocalDatabase(context: Application): LocalDatabase =
        with(context) {
            Room.databaseBuilder(
                context,
                LocalDatabase::class.java,
                getString(R.string.local_db_name)
            )
                .addTypeConverter(Converters())
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
        }

    @Provides
    @BaseProjectUrl
    fun provideBaseUrl(
        environmentRepository: EnvironmentRepository
    ) = when (environmentRepository.currentEnvironment) {
        Dev -> "https://22jwmm93j9.execute-api.us-east-1.amazonaws.com"
        Prod -> "https://w5l4faau04.execute-api.us-east-1.amazonaws.com/"
        LocalEmulator -> "http://10.0.2.2:4000"
    }

    @Provides
    @AuthConfigFile
    @RawRes
    fun provideAuthConfigFile(
        environmentRepository: EnvironmentRepository
    ): Int = when (environmentRepository.currentEnvironment) {
        Dev -> R.raw.dev_amplifyconfiguration
        Prod -> R.raw.prod_amplifyconfiguration
        LocalEmulator -> R.raw.dev_amplifyconfiguration
    }

    @Provides
    fun provideOkHttpClient(authIneractor: AuthInteractor, @AuthConfigFile configFileResourceId: Int?): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(authIneractor))
            .addInterceptor(ErrorInterceptor(authIneractor, configFileResourceId))
            .addInterceptor(LogInterceptor())
            .connectTimeout(30L, TimeUnit.SECONDS)
            .readTimeout(30L, TimeUnit.SECONDS)
            .build()

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