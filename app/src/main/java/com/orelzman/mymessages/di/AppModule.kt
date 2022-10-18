package com.orelzman.mymessages.di

import android.app.Application
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
import com.orelzman.mymessages.di.annotation.AuthConfigFile
import com.orelzman.mymessages.di.annotation.BaseProjectUrl
import com.orelzman.mymessages.di.annotation.DatadogConfigFile
import com.orelzman.mymessages.di.annotation.MixpanelConfigFile
import com.orelzman.mymessages.domain.interactors.CallLogInteractor
import com.orelzman.mymessages.domain.interactors.PhoneCallsInteractor
import com.orelzman.mymessages.domain.model.entities.ConfigFile
import com.orelzman.mymessages.domain.system.phone_call.CallLogObserver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

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
    ): String = when (environmentRepository.currentEnvironment) {
        Dev -> "https://22jwmm93j9.execute-api.us-east-1.amazonaws.com/"
        Prod -> "https://w5l4faau04.execute-api.us-east-1.amazonaws.com/"
        LocalEmulator -> "http://10.0.2.2:4000/"
    }

    @Provides
    @MixpanelConfigFile
    fun provideMixpanelToken(
        environmentRepository: EnvironmentRepository
    ): ConfigFile = when (environmentRepository.currentEnvironment) {
        Dev, LocalEmulator -> ConfigFile(fileResId = R.raw.dev_mixpanel_config)
        Prod -> ConfigFile(fileResId = R.raw.prod_mixpanel_config)
    }

    @Provides
    @DatadogConfigFile
    fun provideDatadogConfig(
        environmentRepository: EnvironmentRepository
    ): ConfigFile = when (environmentRepository.currentEnvironment) {
        Dev, LocalEmulator -> ConfigFile(fileResId = R.raw.dev_datadog_config)
        Prod -> ConfigFile(fileResId = R.raw.prod_datadog_config)
    }

    @Provides
    @AuthConfigFile
    fun provideAuthConfigFile(
        environmentRepository: EnvironmentRepository
    ): ConfigFile = when (environmentRepository.currentEnvironment) {
        Dev, LocalEmulator -> ConfigFile(fileResId = R.raw.dev_amplifyconfiguration)
        Prod -> ConfigFile(fileResId = R.raw.prod_amplifyconfiguration)
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

    @Provides
    fun providesCallLogObserver(
        callLogInteractor: CallLogInteractor,
        phoneCallsInteractor: PhoneCallsInteractor
    ): CallLogObserver = CallLogObserver(
        callLogInteractor = callLogInteractor,
        phoneCallsInteractor = phoneCallsInteractor,
        handler = null
    )
}