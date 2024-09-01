package com.orels.data.remote

import com.orels.domain.repository.EnvironmentRepository
import com.orels.domain.repository.Environments
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EnvironmentRepositoryImpl @Inject constructor() : EnvironmentRepository {
    override val currentEnvironment: Environments = Environments.LocalEmulator
}
