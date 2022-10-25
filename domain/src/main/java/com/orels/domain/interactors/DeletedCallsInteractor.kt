package com.orels.domain.interactors

import com.orels.domain.model.entities.DeletedCall
import kotlinx.coroutines.flow.Flow
import java.util.*

interface DeletedCallsInteractor {
    suspend fun init(fromDate: Date)
    suspend fun create(deletedCall: DeletedCall)
    suspend fun getAll(startDate: Date): Flow<List<DeletedCall>>
    fun getAllOnce(startDate: Date? = null): List<DeletedCall>
}