package com.orelzman.auth.di

import com.orelzman.auth.data.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {

    @Singleton
    @Provides
    fun provideAuthRepository(authRepository: AuthRepository): AuthRepository =
        authRepository
}