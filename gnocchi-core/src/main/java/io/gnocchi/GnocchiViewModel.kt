package io.gnocchi

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class GnocchiViewModel<ACTION, STATE, EVENT>(initState: STATE) : ViewModel() {

    protected open var oldState: STATE = initState

    private val _state = MutableStateFlow(value = initState)
    val state: StateFlow<STATE> = _state.asStateFlow()
    private val _events = Channel<EVENT>(Channel.BUFFERED)
    val events: Flow<EVENT> = _events.receiveAsFlow()

    private val _actions = Channel<ACTION>(Channel.BUFFERED)

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    protected abstract fun processAction(action: ACTION)

    init {
        coroutineScope.launch {
            _actions.consumeEach { action ->
                processAction(action)
            }
        }
    }

    protected open fun updateState(newState: STATE) {
        oldState = _state.value
        _state.value = newState
    }

    protected fun sendEvent(event: EVENT): Job = coroutineScope.launch {
        _events.send(event)
    }

    fun action(action: ACTION): Job = coroutineScope.launch {
        _actions.send(action)
    }
}