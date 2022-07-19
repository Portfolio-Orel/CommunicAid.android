package com.orelzman.mymessages.di

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.orelzman.auth.data.interactor.AuthInteractorImpl
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.data.interactors.*
import com.orelzman.mymessages.data.remote.repository.api.APIRepository
import com.orelzman.mymessages.domain.interactors.*
import com.orelzman.mymessages.domain.managers.UnhandledCallsManager
import com.orelzman.mymessages.domain.managers.UnhandledCallsManagerImpl
import com.orelzman.mymessages.domain.repository.Repository
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
    abstract fun provideRepository(repository: APIRepository): Repository

    @Binds
    abstract fun provideMessageInteractor(interactor: MessageInteractorImpl): MessageInteractor

    @Binds
    abstract fun provideFolderInteractor(interactor: FolderInteractorImpl): FolderInteractor

    @Binds
    abstract fun provideMessageInFolderInteractor(interactor: MessageInFolderInteractorImpl): MessageInFolderInteractor

    @Binds
    abstract fun providePhoneCallsInteractor(interactor: PhoneCallsInteractorImpl): PhoneCallsInteractor

    @Binds
    abstract fun provideUnhandledCallsInteractor(unhandledCallsInteractor: DeletedCallsInteractorImpl): DeletedCallsInteractor

    @ExperimentalPermissionsApi
    @Binds
    abstract fun providePhoneCallManager(manager: PhoneCallManagerImpl): PhoneCallManager

    @ExperimentalPermissionsApi
    @Binds
    abstract fun providePhoneCallManagerInteractor(interactor: PhoneCallManagerInteractorImpl): PhoneCallManagerInteractor

    @Binds
    abstract fun provideDatabaseInteractor(interactor: DatabaseInteractorImpl): DatabaseInteractor

    @Binds
    abstract fun provideAnalyticsInteractor(interactor: AnalyticsInteractorImpl): AnalyticsInteractor

    @Binds
    abstract fun provideSettingsInteractor(interactor: SettingsInteractorImpl): SettingsInteractor

    @Binds
    abstract fun provideUnhandledCallsManager(manager: UnhandledCallsManagerImpl): UnhandledCallsManager

    @Binds
    abstract fun provideDataSourceCallsInteractor(interactorImpl: DataSourceCallsInteractorImpl): DataSourceCallsInteractor

    @Binds
    abstract fun provideCallLogInteractor(interactorImpl: CallLogInteractorImpl): CallLogInteractor
}