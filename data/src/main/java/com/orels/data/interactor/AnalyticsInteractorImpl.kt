package com.orels.data.interactor

import android.content.Context
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.orels.auth.domain.interactor.AuthInteractor
import com.orels.domain.annotation.MixpanelConfigFile
import com.orels.domain.interactors.AnalyticsIdentifiers
import com.orels.domain.interactors.AnalyticsInteractor
import com.orels.domain.model.entities.ConfigFile
import com.orels.domain.util.extension.rawResToStringMap
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import javax.inject.Inject

class AnalyticsInteractorImpl @Inject constructor(
    @ApplicationContext context: Context,
    @MixpanelConfigFile mixpanelConfigFile: ConfigFile,
    private val authInteractor: AuthInteractor
) : AnalyticsInteractor {
    private val mixpanel: MixpanelAPI

    init {
        val token =
            context.rawResToStringMap(mixpanelConfigFile.fileResId).getOrDefault("token", "")
        mixpanel = MixpanelAPI.getInstance(context, token)
    }

    override fun track(identifier: AnalyticsIdentifiers, value: Map<String, Any>) =
        trackWithUsername(identifier, listOf(value))

    override fun track(identifier: AnalyticsIdentifiers, values: List<Map<String, Any>>) =
        trackWithUsername(identifier, values)

    private fun trackWithUsername(
        identifier: AnalyticsIdentifiers,
        values: List<Map<String, Any>>
    ) {
        mixpanel.track(
            identifier.identifier,
            values.asJson(
                extras = mapOf(
                    "username" to ((authInteractor.getUser()?.username) ?: "")
                )
            )
        )
    }
}

fun List<Map<String, Any>>.asJson(extras: Map<String, Any> = emptyMap()): JSONObject {
    val json = JSONObject()
    val allValues = ArrayList(this)
    allValues.add(extras)
    allValues.forEach {
        it.forEach { (key, value) ->
            json.put(key, value)
        }
    }
    return json
}