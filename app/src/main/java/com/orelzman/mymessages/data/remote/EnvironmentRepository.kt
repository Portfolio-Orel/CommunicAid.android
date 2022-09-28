package com.orelzman.mymessages.data.remote

import com.orelzman.mymessages.BuildConfig
import javax.inject.Inject

class EnvironmentRepository @Inject constructor() {
    val currentEnvironment: Environments =
        if (BuildConfig.DEBUG) Environments.Prod else Environments.Prod
}

enum class Environments {
    Dev,
    Prod,
    LocalEmulator
}