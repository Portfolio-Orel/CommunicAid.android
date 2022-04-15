package com.orelzman.mymessages.di

import com.orelzman.auth.data.interactor.AuthInteractorImpl
import com.orelzman.auth.domain.interactor.AuthInteractor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
 // Here I will bind firestore repository and the auth.
    @Binds
    @Singleton
    abstract fun bindAuthInteractor(
        authInteractorImpl: AuthInteractorImpl
    ): AuthInteractor
}