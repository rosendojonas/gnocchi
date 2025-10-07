package io.gnocchi_android_view


import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

fun <STATE> Fragment.observeStates(stateFlow: StateFlow<STATE>, onChanged: (t: STATE) -> Unit) {
    lifecycleScope.launch {
        stateFlow.flowWithLifecycle(lifecycle = lifecycle).collect {
            onChanged(it)
        }
    }
}

fun <STATE> ComponentActivity.observeStates(stateFlow: StateFlow<STATE>, onChanged: (t: STATE) -> Unit) {
    lifecycleScope.launch {
        stateFlow.flowWithLifecycle(lifecycle = lifecycle).collect {
            onChanged(it)
        }
    }
}

inline fun <EVENT> Fragment.observeEvents(
    events: Flow<EVENT>,
    crossinline onEvent: (EVENT) -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            events.collect { onEvent(it) }
        }
    }
}

inline fun <EVENT> ComponentActivity.observeEvents(
    events: Flow<EVENT>,
    crossinline onEvent: (EVENT) -> Unit
) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            events.collect { onEvent(it) }
        }
    }
}