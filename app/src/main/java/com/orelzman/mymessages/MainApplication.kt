package com.orelzman.mymessages

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.datadog.android.Datadog
import com.datadog.android.core.configuration.Credentials
import com.datadog.android.privacy.TrackingConsent
import com.datadog.android.rum.GlobalRum
import com.datadog.android.rum.RumMonitor
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.data.remote.EnvironmentRepository
import com.orels.data.annotation.AuthConfigFile
import com.orels.data.annotation.DatadogConfigFile
import com.orelzman.mymessages.domain.model.entities.ConfigFile
import com.orelzman.mymessages.domain.system.phone_call.PhonecallReceiver
import com.orelzman.mymessages.domain.system.phone_call.SettingsPhoneCallReceiver
import com.orelzman.mymessages.domain.util.extension.log
import com.orelzman.mymessages.domain.util.extension.rawResToStringMap
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
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
    lateinit var environmentRepository: EnvironmentRepository

    @Inject
    lateinit var authInteractor: AuthInteractor

    override fun onCreate() {
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
            envName = environmentRepository.currentEnvironment.name,
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
        CoroutineScope(Dispatchers.Default).launch {
            try {
                authInteractor.init(configFileResourceId = authConfigFile.fileResId)
            } catch (e: Exception) {
                e.log()
            }
            authInteractor.getUserFlow().collectLatest { user ->
                if (authInteractor.isAuthorized(user)) {
                    PhonecallReceiver.enable(context = applicationContext)
                    SettingsPhoneCallReceiver.enable(context = applicationContext)
                } else {
                    PhonecallReceiver.disable(context = applicationContext)
                    SettingsPhoneCallReceiver.disable(context = applicationContext)
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