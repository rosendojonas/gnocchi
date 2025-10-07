package io.gnocchi_compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun <T> rememberGnocchiState(stateFlow: StateFlow<T>): State<T>  {
    return stateFlow.collectAsStateWithLifecycle()
}

/**
 * Collects Flow events respecting STARTED, ideal for one-shots (snackbar, navigation, toast and so).
 */
@Composable
inline fun <EVENT> ObserveEvents(
    events: Flow<EVENT>,
    crossinline onEvent: (EVENT) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(events, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            events.collect { onEvent(it) }
        }
    }
}
