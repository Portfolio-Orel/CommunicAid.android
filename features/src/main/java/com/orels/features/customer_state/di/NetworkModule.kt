package com.orels.features.customer_state.di

import com.orels.features.customer_state.data.remote.API
import com.orels.features.customer_state.data.remote.CustomerStateRepositoryImpl
import com.orels.features.customer_state.domain.annotation.CustomerStateOkHttp
import com.orels.features.customer_state.domain.annotation.Token
import com.orels.features.customer_state.domain.repostiory.CustomerStateRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Created by Orel Zilberman on 30/03/2024.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @Token
    fun provideToken(): String = "xoKKYIAUmRZVbta2DHaiC2Fmcy9som0J"

    @Provides
    @Singleton
    @CustomerStateOkHttp
    fun provideOkHttpClient(@Token token: String): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Token", token)
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(@CustomerStateOkHttp okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://deepsiam.club/") // Replace with your base URL
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun provideAPI(retrofit: Retrofit): API =
        retrofit.create(API::class.java)

    @Provides
    @Singleton
    fun provideAPIRepository(api: API): CustomerStateRepository = CustomerStateRepositoryImpl(api)
}
