package com.orels.domain.util.extension

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * @author Orel Zilberman
 * 08/09/2022
 */

typealias CoroutineFunction = suspend CoroutineScope.() -> Unit

fun CoroutineScope.launchCatching(
    dispatcher: CoroutineContext = Dispatchers.Default,
    block: CoroutineFunction,
) {
    launch(dispatcher) {
        val result = kotlin.runCatching { block() }
        if (result.isFailure) {
            result.exceptionOrNull()?.log()
        }
    }
}