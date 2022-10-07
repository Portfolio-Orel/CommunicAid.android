package com.orels.auth.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AuthModuleDependencies {

    @com.orels.auth.di.AuthRepository
    fun provideAuthRepository(authRepository: AuthRepository)
}