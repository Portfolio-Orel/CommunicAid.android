package com.orelzman.mymessages.di

import com.orelzman.auth.data.interactor.AuthInteractorImpl
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.data.repository.Repository
import com.orelzman.mymessages.data.repository.firebase.FirebaseRepository
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

    /**
     * @Instructions
     * Has to be injected after login was successful!
     * ( Not in the login screen or before)
     */
    @Binds
    @Singleton
    abstract fun provideFirebaseRepository(firebaseRepository: FirebaseRepository): Repository

}