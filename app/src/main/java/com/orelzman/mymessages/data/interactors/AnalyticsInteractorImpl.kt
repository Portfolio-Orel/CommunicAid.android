package com.orelzman.mymessages.data.interactors

import android.content.Context
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.di.annotation.MixpanelConfigFile
import com.orelzman.mymessages.domain.interactors.AnalyticsIdentifiers
import com.orelzman.mymessages.domain.interactors.AnalyticsInteractor
import com.orelzman.mymessages.domain.model.entities.ConfigFile
import com.orelzman.mymessages.domain.util.extension.rawResToStringMap
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
        mixpanel.track(identifier.identifier, value.asJson())

    override fun track(identifier: AnalyticsIdentifiers, values: List<Map<String, Any>>) =
        mixpanel.track(identifier.identifier, values.asJson())
}

fun Map<String, Any>.asJson(): JSONObject {
    val props = JSONObject()
    forEach { (key, value) ->
        props.put(key, value)
    }
    return props
}


fun List<Map<String, Any>>.asJson(): JSONObject {
    val props = JSONObject()
    forEach {
        it.forEach { (key, value) ->
            props.put(key, value)
        }
    }
    return props
}