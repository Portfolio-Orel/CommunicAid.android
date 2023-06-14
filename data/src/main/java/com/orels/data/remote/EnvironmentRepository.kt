package com.orels.data.remote

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EnvironmentRepository @Inject constructor() {
    val currentEnvironment: Environments = Environments.LocalEmulator
}

enum class Environments {
    Dev,
    Prod,
    LocalEmulator
}