package com.orelzman.auth.di

import com.orelzman.auth.data.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object Module {

    @com.orelzman.auth.di.AuthRepository
    @Provides
    fun provideAuthRepository(): AuthRepository =
        AuthRepository()
}