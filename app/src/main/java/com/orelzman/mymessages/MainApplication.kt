package com.orelzman.mymessages

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.datadog.android.Datadog
import com.datadog.android.core.configuration.Credentials
import com.datadog.android.privacy.TrackingConsent
import com.datadog.android.rum.GlobalRum
import com.datadog.android.rum.RumMonitor
import com.google.gson.Gson
import com.orelzman.mymessages.data.remote.EnvironmentRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class MainApplication : Application(), Configuration.Provider {

    companion object {
        const val DataDogServiceName = "Android MyMessages"
        const val DataDogVariantName = "Variant Android MyMessages"
    }

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var environmentRepository: EnvironmentRepository
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
            variant = DataDogVariantName,
            rumApplicationId = applicationId,
            serviceName = DataDogServiceName
        )
        Datadog.initialize(this, credentials, configuration, TrackingConsent.GRANTED)
        GlobalRum.registerIfAbsent(RumMonitor.Builder().build())
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    @Suppress("UNCHECKED_CAST")
    private fun getDatadogConfig(): Map<String, String> {
        val dataString = resources.openRawResource(R.raw.datadog_config)
            .bufferedReader()
            .use { it.readText() }
        return Gson().fromJson(dataString, Map::class.java) as? Map<String, String>
            ?: return emptyMap()
    }

    private fun resolveDatadogClientToken(map: Map<String, String>): String =
        map["clientToken"] ?: ""

    private fun resolveDatadogApplicationId(map: Map<String, String>): String =
        map["applicationId"] ?: ""
}