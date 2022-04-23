package com.orelzman.mymessages.di

import com.orelzman.auth.data.interactor.AuthInteractorImpl
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.data.local.interactors.folder.FolderInteractor
import com.orelzman.mymessages.data.local.interactors.folder.FolderInteractorImpl
import com.orelzman.mymessages.data.local.interactors.message.MessageInteractor
import com.orelzman.mymessages.data.local.interactors.message.MessageInteractorImpl
import com.orelzman.mymessages.data.repository.Repository
import com.orelzman.mymessages.data.repository.firebase.FirebaseRepository
import com.orelzman.mymessages.domain.service.PhoneCall.PhoneCallInteractor
import com.orelzman.mymessages.domain.service.PhoneCall.PhoneCallInteractorImpl
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
     * Instructions are irrelevant until further noticed.
     * @Instructions
     * Has to be injected after login was successful!
     * ( Not in the login screen or before)
     */
    @Binds
    @Singleton
    abstract fun provideRepository(firebaseRepository: FirebaseRepository): Repository

    @Binds
    @Singleton
    abstract fun provideFolderInteractor(interactor: MessageInteractorImpl): MessageInteractor

    @Binds
    @Singleton
    abstract fun provideMessageInteractor(interactor: FolderInteractorImpl): FolderInteractor

    @Binds
    @Singleton
    abstract fun providePhoneCallInteractor(phoneCallInteractorImpl: PhoneCallInteractorImpl): PhoneCallInteractor
}