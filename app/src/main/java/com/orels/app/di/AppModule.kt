package com.orels.app.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.orels.R
import com.orels.data.interactor.UserInteractorImpl
import com.orels.data.interceptor.AuthInterceptor
import com.orels.data.interceptor.ErrorInterceptor
import com.orels.data.interceptor.LogInterceptor
import com.orels.data.local.AuthDatabase
import com.orels.data.local.LocalDatabase
import com.orels.data.local.dao.UserDao
import com.orels.data.local.type_converters.Converters
import com.orels.data.remote.EnvironmentRepository
import com.orels.data.remote.Environments
import com.orels.data.remote.repository.api.API
import com.orels.domain.annotation.AuthConfigFile
import com.orels.domain.annotation.BaseProjectUrl
import com.orels.domain.annotation.DatadogConfigFile
import com.orels.domain.annotation.MixpanelConfigFile
import com.orels.domain.interactors.AuthInteractor
import com.orels.domain.interactors.UserInteractor
import com.orels.domain.model.entities.ConfigFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

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
    fun provideUserInteractor(userInteractor: UserInteractorImpl): UserInteractor = userInteractor

//    @Provides
//    @Singleton
//    fun provideGoogleSignInClient(@ApplicationContext context: Context): GoogleSignInClient {
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken("670361895848-0jildiu2ebiip55tqnkdtuhm1oq5mujc.apps.googleusercontent.com")
//            .requestEmail()
//            .build()
//        return GoogleSignIn.getClient(context, gso)
//    }
//
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
    ): String = when (environmentRepository.currentEnvironment) {
        Environments.Dev -> "https://22jwmm93j9.execute-api.us-east-1.amazonaws.com/"
        Environments.Prod -> "https://w5l4faau04.execute-api.us-east-1.amazonaws.com/"
        Environments.LocalEmulator -> "http://10.0.2.2:4000/"
    }

    @Provides
    @MixpanelConfigFile
    fun provideMixpanelToken(
        environmentRepository: EnvironmentRepository
    ): ConfigFile = when (environmentRepository.currentEnvironment) {
        Environments.Dev, Environments.LocalEmulator -> ConfigFile(fileResId = R.raw.dev_mixpanel_config)
        Environments.Prod -> ConfigFile(fileResId = R.raw.prod_mixpanel_config)
    }

    @Provides
    @DatadogConfigFile
    fun provideDatadogConfig(
        environmentRepository: EnvironmentRepository
    ): ConfigFile = when (environmentRepository.currentEnvironment) {
        Environments.Dev, Environments.LocalEmulator -> ConfigFile(fileResId = R.raw.dev_datadog_config)
        Environments.Prod -> ConfigFile(fileResId = R.raw.prod_datadog_config)
    }

    @Provides
    @AuthConfigFile
    fun provideAuthConfigFile(
        environmentRepository: EnvironmentRepository
    ): ConfigFile = when (environmentRepository.currentEnvironment) {
        Environments.Dev, Environments.LocalEmulator -> ConfigFile(fileResId = R.raw.dev_amplifyconfiguration)
        Environments.Prod -> ConfigFile(fileResId = R.raw.prod_amplifyconfiguration)
    }

    @Provides
    fun provideOkHttpClient(
        authIneractor: AuthInteractor,
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(authIneractor))
            .addInterceptor(ErrorInterceptor(authIneractor))
            .addInterceptor(LogInterceptor())
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