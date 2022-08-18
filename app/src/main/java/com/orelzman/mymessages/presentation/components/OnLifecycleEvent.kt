package com.orelzman.mymessages.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

/**
 * @author Orel Zilberman
 * 17/08/2022
 */

@Composable
fun OnLifecycleEvent(
    onStart: () -> Unit = {},
    onResume: () -> Unit = {},
    onPause: () -> Unit = {},
    onCreate: () -> Unit = {},
    onStop: () -> Unit = {},
    onDestroy: () -> Unit = {},
    onAny: () -> Unit = {},
) {
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            when(event) {
                Lifecycle.Event.ON_START -> {
                    onStart()
                }
                Lifecycle.Event.ON_RESUME -> {
                    onResume()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    onPause()
                }
                Lifecycle.Event.ON_CREATE -> {
                    onCreate()
                }
                Lifecycle.Event.ON_STOP -> {
                    onStop()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    onDestroy()
                }
                Lifecycle.Event.ON_ANY -> {
                    onAny()
                }
            }
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}