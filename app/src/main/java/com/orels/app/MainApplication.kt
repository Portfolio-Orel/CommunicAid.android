package com.orels.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.datadog.android.Datadog
import com.datadog.android.core.configuration.Credentials
import com.datadog.android.privacy.TrackingConsent
import com.datadog.android.rum.GlobalRum
import com.datadog.android.rum.RumMonitor
import com.google.firebase.FirebaseApp
import com.orels.data.remote.EnvironmentRepositoryImpl
import com.orels.domain.annotation.AuthConfigFile
import com.orels.domain.annotation.DatadogConfigFile
import com.orels.auth.domain.interactor.AuthInteractor
import com.orels.auth.domain.interactor.UserState
import com.orels.domain.model.entities.ConfigFile
import com.orels.domain.system.phone_call.PhonecallReceiver
import com.orels.domain.util.extension.log
import com.orels.domain.util.extension.rawResToStringMap
import com.orels.domain.util.extension.safeCollectLatest
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltAndroidApp
class MainApplication : Application(), Configuration.Provider {

    @Inject
    @DatadogConfigFile
    lateinit var datadogConfigFile: ConfigFile

    @Inject
    @AuthConfigFile
    lateinit var authConfigFile: ConfigFile

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var environmentRepositoryImpl: EnvironmentRepositoryImpl

    @Inject
    lateinit var authInteractor: AuthInteractor

    override fun onCreate() {
        FirebaseApp.initializeApp(this)
        super.onCreate()
        val configuration = com.datadog.android.core.configuration.Configuration.Builder(
            logsEnabled = true,
            tracesEnabled = true,
            crashReportsEnabled = true,
            rumEnabled = true
        ).build()
        val dataDogConfig = getDatadogConfig()
        val clientToken = resolveDatadogClientToken(dataDogConfig)
        val applicationId = resolveDatadogApplicationId(dataDogConfig)
        val credentials = Credentials(
            clientToken = clientToken,
            envName = environmentRepositoryImpl.currentEnvironment.name,
            variant = "MyMessagesVariant",
            rumApplicationId = applicationId,
            serviceName = "MyMessagesService"
        )
        Datadog.initialize(this, credentials, configuration, TrackingConsent.GRANTED)
        GlobalRum.registerIfAbsent(RumMonitor.Builder().build())
        observeUser()
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    private fun observeUser() {
        CoroutineScope(SupervisorJob()).launch {
            try {
                authInteractor.initialize(configFileResourceId = authConfigFile.fileResId)
            } catch (e: Exception) {
                e.log()
            }
            authInteractor.getUserState().safeCollectLatest { state ->
                try {
                    if (state == UserState.LoggedIn) {
                        PhonecallReceiver.enable(context = applicationContext)
                        SettingsPhoneCallReceiver.enable(context = applicationContext)
                    } else {
                        PhonecallReceiver.disable(context = applicationContext)
                        SettingsPhoneCallReceiver.disable(context = applicationContext)
                    }
                } catch(e: Exception) {
                    e.log()
                }
            }
        }
    }

    private fun getDatadogConfig(): Map<String, String> =
        rawResToStringMap(res = datadogConfigFile.fileResId)

    private fun resolveDatadogClientToken(map: Map<String, String>): String =
        map["clientToken"] ?: ""

    private fun resolveDatadogApplicationId(map: Map<String, String>): String =
        map["applicationId"] ?: ""
}