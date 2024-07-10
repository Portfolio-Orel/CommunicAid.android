package com.orels.domain.repository

/**
 * Created by Orel Zilberman on 03/06/2024.
 */


enum class Environments {
    Dev,
    Prod,
    LocalEmulator
}
interface EnvironmentRepository {
    val currentEnvironment: Environments
}