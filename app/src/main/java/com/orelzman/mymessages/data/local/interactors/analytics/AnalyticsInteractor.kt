package com.orelzman.mymessages.data.local.interactors.analytics

import com.orelzman.mymessages.data.dto.Loggable

interface AnalyticsInteractor {

    fun track(identifier: String, data: Map<String, Any>)
    fun track(identifier: String, loggable: Loggable)
    fun track(identifier: String, loggables: List<Loggable>)
    fun track(identifier: String, pair: Pair<String, Any>)
}