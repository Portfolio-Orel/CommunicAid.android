package com.orels.domain.util.extension

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlin.coroutines.cancellation.CancellationException

/**
 * Terminal flow operator that collects the given flow with a provided [action] and catch [CancellationException]
 */
suspend inline fun <T> Flow<T>.safeCollectLatest(
    crossinline onException: (Exception) -> Unit = { it.log() },
    crossinline action: suspend (value: T) -> Unit
): Unit =
    collectLatest { value ->
        try {
            action(value)
        } catch (e: CancellationException) {
            onException(e)
        }
    }