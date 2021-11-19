package com.orelzman.auth.di

import com.orelzman.auth.data.repository.AuthRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object Module {

    @Singleton
    @Provides
    fun provideAuthRepository(authRepository: AuthRepository): AuthRepository =
        authRepository
}