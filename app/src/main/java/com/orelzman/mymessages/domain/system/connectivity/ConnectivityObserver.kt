package com.orelzman.mymessages.domain.system.connectivity

import kotlinx.coroutines.flow.Flow

/**
 * @author Orel Zilberman
 * 19/08/2022
 */
interface ConnectivityObserver {
    fun observe(): Flow<NetworkState>
}

enum class NetworkState {
    Available,
    Losing,
    Lost,
    Unavailable;
}