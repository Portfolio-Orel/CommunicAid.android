package com.orelzman.mymessages.domain.interactors

import com.orelzman.mymessages.domain.model.entities.Loggable

interface AnalyticsInteractor {

    fun track(identifier: String, data: Map<String, Any>)
    fun track(identifier: String, loggable: Loggable)
    fun track(identifier: String, loggables: List<Loggable>)
    fun track(identifier: String, pair: Pair<String, Any>)
}