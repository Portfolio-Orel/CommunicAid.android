package com.orelzman.mymessages.data.remote

import javax.inject.Inject

class EnvironmentRepository @Inject constructor(){
    val currentEnvironment: Environments = Environments.Production
}

enum class Environments {
    Production,
    Local
}