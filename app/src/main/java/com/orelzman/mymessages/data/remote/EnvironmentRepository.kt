package com.orelzman.mymessages.data.remote

import javax.inject.Inject

class EnvironmentRepository @Inject constructor() {
    val currentEnvironment: Environments = Environments.Dev
//        if (BuildConfig.DEBUG)
}

enum class Environments {
    Dev,
    Prod,
    LocalEmulator
}