package com.orelzman.mymessages.di

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.orelzman.auth.data.interactor.AuthInteractorImpl
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.data.interactors.*
import com.orelzman.mymessages.data.remote.repository.api.APIRepository
import com.orelzman.mymessages.domain.annotation.Proximity
import com.orelzman.mymessages.domain.interactors.*
import com.orelzman.mymessages.domain.managers.phonecall.PhoneCallManager
import com.orelzman.mymessages.domain.managers.phonecall.PhoneCallManagerImpl
import com.orelzman.mymessages.domain.managers.phonecall.interactor.PhoneCallManagerInteractor
import com.orelzman.mymessages.domain.managers.phonecall.interactor.PhoneCallManagerInteractorImpl
import com.orelzman.mymessages.domain.managers.system_service.SystemService
import com.orelzman.mymessages.domain.managers.system_service.SystemServiceManager
import com.orelzman.mymessages.domain.managers.system_service.SystemServiceManagerImpl
import com.orelzman.mymessages.domain.managers.unhandled_calls.UnhandledCallsManager
import com.orelzman.mymessages.domain.managers.unhandled_calls.UnhandledCallsManagerImpl
import com.orelzman.mymessages.domain.managers.worker.WorkerManager
import com.orelzman.mymessages.domain.managers.worker.WorkerManagerImpl
import com.orelzman.mymessages.domain.repository.Repository
import com.orelzman.mymessages.domain.system.connectivity.ConnectivityObserver
import com.orelzman.mymessages.domain.system.connectivity.ConnectivityObserverObserverImpl
import com.orelzman.mymessages.domain.system.proximity.ProximityManagerImpl
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
    abstract fun provideConnectivityObserver(connectivityObserver: ConnectivityObserverObserverImpl): ConnectivityObserver

    @Binds
    abstract fun provideRepository(repository: APIRepository): Repository

    @Binds
    abstract fun provideMessageInteractor(interactorImpl: MessageInteractorImpl): MessageInteractor

    @Binds
    abstract fun provideFolderInteractor(interactorImpl: FolderInteractorImpl): FolderInteractor

    @Binds
    abstract fun provideMessageInFolderInteractor(interactorImpl: MessageInFolderInteractorImpl): MessageInFolderInteractor

    @Binds
    abstract fun providePhoneCallsInteractor(interactorImpl: PhoneCallsInteractorImpl): PhoneCallsInteractor

    @Binds
    abstract fun provideUnhandledCallsInteractor(interactorImpl: DeletedCallsInteractorImpl): DeletedCallsInteractor

    @ExperimentalPermissionsApi
    @Binds
    abstract fun providePhoneCallManager(manager: PhoneCallManagerImpl): PhoneCallManager

    @ExperimentalPermissionsApi
    @Binds
    abstract fun providePhoneCallManagerInteractor(interactorImpl: PhoneCallManagerInteractorImpl): PhoneCallManagerInteractor

    @Binds
    abstract fun provideDatabaseInteractor(interactorImpl: GeneralInteractorImpl): GeneralInteractor

    @Binds
    abstract fun provideAnalyticsInteractor(interactorImpl: AnalyticsInteractorImpl): AnalyticsInteractor

    @Binds
    abstract fun provideSettingsInteractor(interactorImpl: SettingsInteractorImpl): SettingsInteractor

    @Binds
    abstract fun provideUnhandledCallsManager(manager: UnhandledCallsManagerImpl): UnhandledCallsManager

    @Binds
    abstract fun provideDataSourceCallsInteractor(interactorImpl: DataSourceCallsInteractorImpl): DataSourceCallsInteractor

    @Binds
    abstract fun provideCallLogInteractor(interactorImpl: CallLogInteractorImpl): CallLogInteractor

    @Binds
    @Singleton
    @Proximity
    abstract fun provideProximityManager(managerImpl: ProximityManagerImpl): SystemService

    @Binds
    abstract fun provideSystemServiceManager(managerImpl: SystemServiceManagerImpl): SystemServiceManager

    @Binds
    abstract fun provideStatisticsInteractor(interactorImpl: StatisticsInteractorImpl): StatisticsInteractor

    @Binds
    abstract fun provideWhatsappInteractor(interactorImpl: WhatsappInteractorImpl): WhatsappInteractor

    @Binds
    @Singleton
    abstract fun provideWorkerManager(managerImpl: WorkerManagerImpl): WorkerManager
}