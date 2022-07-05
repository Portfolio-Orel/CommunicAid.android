package com.orelzman.mymessages.data.local.interactors.analytics

import android.content.Context
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.orelzman.mymessages.domain.interactors.AnalyticsInteractor
import com.orelzman.mymessages.domain.model.entities.Loggable
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import javax.inject.Inject

class AnalyticsInteractorImpl @Inject constructor(
    @ApplicationContext context: Context
) : AnalyticsInteractor {
    private val mixpanel: MixpanelAPI =
        MixpanelAPI.getInstance(context, "1922ffdaa9090167dd5b313cdd3a64b6")

    override fun track(identifier: String, data: Map<String, Any>) {
        val props = JSONObject()
        data.forEach { (key, value) ->
            props.put(key, value)
        }
        mixpanel.track(identifier, props)
    }

    override fun track(identifier: String, loggable: Loggable) =
        track(identifier, loggable.data)


    override fun track(identifier: String, loggables: List<Loggable>) =
        loggables.forEach { loggable ->
            track(identifier, loggable)
        }

    override fun track(identifier: String, pair: Pair<String, Any>) =
        track(identifier, mapOf(pair.first to pair.second))

}