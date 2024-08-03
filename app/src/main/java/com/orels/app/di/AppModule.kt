package com.orels.app.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.orels.R
import com.orels.auth.data.local.AuthDatabase
import com.orels.auth.data.local.dao.UserDao
import com.orels.auth.domain.interactor.AuthInteractor
import com.orels.data.interceptor.AuthInterceptor
import com.orels.data.interceptor.ErrorInterceptor
import com.orels.data.interceptor.LogInterceptor
import com.orels.data.interceptor.ResponseInterceptor
import com.orels.data.local.LocalDatabase
import com.orels.data.local.type_converters.Converters
import com.orels.data.remote.EnvironmentRepositoryImpl
import com.orels.data.remote.repository.api.API
import com.orels.domain.annotation.AuthConfigFile
import com.orels.domain.annotation.BaseProjectUrl
import com.orels.domain.annotation.DatadogConfigFile
import com.orels.domain.annotation.MixpanelConfigFile
import com.orels.domain.model.entities.ConfigFile
import com.orels.domain.repository.Environments
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val AUTH_DB_NAME = "Auth_DB"

    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AuthDatabase =
        Room.databaseBuilder(
            context,
            AuthDatabase::class.java,
            AUTH_DB_NAME
        )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()

    @Provides
    fun provideUserDB(db: AuthDatabase): UserDao =
        db.userDao()

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
        environmentRepositoryImpl: EnvironmentRepositoryImpl
    ): String = when (environmentRepositoryImpl.currentEnvironment) {
        Environments.Dev -> "https://yrwk8oskf9.execute-api.us-east-1.amazonaws.com/"
        Environments.Prod -> "https://yrwk8oskf9.execute-api.us-east-1.amazonaws.com/"
        Environments.LocalEmulator -> "http://10.100.102.7:4871/"
    }

    @Provides
    @MixpanelConfigFile
    fun provideMixpanelToken(
        environmentRepositoryImpl: EnvironmentRepositoryImpl
    ): ConfigFile = when (environmentRepositoryImpl.currentEnvironment) {
        Environments.Dev, Environments.LocalEmulator -> ConfigFile(fileResId = R.raw.dev_mixpanel_config)
        Environments.Prod -> ConfigFile(fileResId = R.raw.prod_mixpanel_config)
    }

    @Provides
    @DatadogConfigFile
    fun provideDatadogConfig(
        environmentRepositoryImpl: EnvironmentRepositoryImpl
    ): ConfigFile = when (environmentRepositoryImpl.currentEnvironment) {
        Environments.Dev, Environments.LocalEmulator -> ConfigFile(fileResId = R.raw.dev_datadog_config)
        Environments.Prod -> ConfigFile(fileResId = R.raw.prod_datadog_config)
    }

    @Provides
    @AuthConfigFile
    fun provideAuthConfigFile(
        environmentRepositoryImpl: EnvironmentRepositoryImpl
    ): ConfigFile = when (environmentRepositoryImpl.currentEnvironment) {
        Environments.Dev, Environments.LocalEmulator -> ConfigFile(fileResId = R.raw.prod_amplifyconfiguration)
        Environments.Prod -> ConfigFile(fileResId = R.raw.prod_amplifyconfiguration)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        authInteractor: AuthInteractor,
        gson: Gson
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(authInteractor = authInteractor))
        .addInterceptor(ErrorInterceptor(authInteractor = authInteractor))
        .addInterceptor(LogInterceptor())
        .addInterceptor(ResponseInterceptor(gson = gson))
        .cache(
            Cache(
                File(context.cacheDir, "http_cache"), 50L * 1024L * 1024L // 50 MiB
            )
        )
        .retryOnConnectionFailure(true)
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