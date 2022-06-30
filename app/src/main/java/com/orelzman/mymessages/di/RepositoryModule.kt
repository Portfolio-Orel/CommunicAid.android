package com.orelzman.mymessages.di

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.orelzman.auth.data.interactor.AuthInteractorImpl
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.data.local.interactors.folder.FolderInteractor
import com.orelzman.mymessages.data.local.interactors.folder.FolderInteractorImpl
import com.orelzman.mymessages.data.local.interactors.message.MessageInteractor
import com.orelzman.mymessages.data.local.interactors.message.MessageInteractorImpl
import com.orelzman.mymessages.data.local.interactors.message_in_folder.MessageInFolderInteractor
import com.orelzman.mymessages.data.local.interactors.message_in_folder.MessageInFolderInteractorImpl
import com.orelzman.mymessages.data.local.interactors.phoneCall.PhoneCallsInteractor
import com.orelzman.mymessages.data.local.interactors.phoneCall.PhoneCallsInteractorImpl
import com.orelzman.mymessages.data.local.interactors.unhandled_calls.UnhandledCallsInteractor
import com.orelzman.mymessages.data.local.interactors.unhandled_calls.UnhandledCallsInteractorImpl
import com.orelzman.mymessages.data.remote.repository.api.APIRepository
import com.orelzman.mymessages.data.remote.repository.api.Repository
import com.orelzman.mymessages.domain.service.phone_call.PhoneCallManager
import com.orelzman.mymessages.domain.service.phone_call.PhoneCallManagerImpl
import com.orelzman.mymessages.domain.service.phone_call.PhoneCallManagerInteractor
import com.orelzman.mymessages.domain.service.phone_call.PhoneCallManagerInteractorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthInteractor(
        authInteractorImpl: AuthInteractorImpl
    ): AuthInteractor

    @Binds
    @Singleton
    abstract fun provideRepository(repository: APIRepository): Repository

    @Binds
    @Singleton
    abstract fun provideMessageInteractor(interactor: MessageInteractorImpl): MessageInteractor

    @Binds
    @Singleton
    abstract fun provideFolderInteractor(interactor: FolderInteractorImpl): FolderInteractor

    @Binds
    @Singleton
    abstract fun provideMessageInFolderInteractor(interactor: MessageInFolderInteractorImpl): MessageInFolderInteractor

    @Binds
    @Singleton
    abstract fun providePhoneCallsInteractor(interactor: PhoneCallsInteractorImpl): PhoneCallsInteractor

    @Binds
    @Singleton
    abstract fun provideUnhandledCallsInteractor(unhandledCallsInteractor: UnhandledCallsInteractorImpl): UnhandledCallsInteractor

    @ExperimentalPermissionsApi
    @Binds
    @Singleton
    abstract fun providePhoneCallManager(manager: PhoneCallManagerImpl): PhoneCallManager

    @ExperimentalPermissionsApi
    @Binds
    @Singleton
    abstract fun providePhoneCallManagerInteractor(interactor: PhoneCallManagerInteractorImpl): PhoneCallManagerInteractor

}