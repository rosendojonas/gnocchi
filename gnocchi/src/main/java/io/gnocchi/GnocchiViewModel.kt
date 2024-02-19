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

/**
 * A -> Action
 * E -> Event
 * S - > State
 * */
public abstract class GnocchiViewModel<A, S, E> : ViewModel() {

    protected abstract val initState: S

    protected open var oldState: S = initState

    private val _state = MutableStateFlow(value = initState)
    public val state: StateFlow<S> = _state.asStateFlow()

    private val _events = Channel<E>(Channel.BUFFERED)
    public val events: Flow<E> = _events.receiveAsFlow()

    private val _actions = Channel<A>(Channel.BUFFERED)

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    protected abstract fun processAction(action: A)

    init {
        coroutineScope.launch {
            _actions.consumeEach { action ->
                processAction(action)
            }
        }
    }

    protected open fun updateState(newState: S) {
        oldState = _state.value
        _state.value = newState
    }

    protected fun sendEvent(event: E): Job = coroutineScope.launch {
        _events.send(event)
    }

    public fun action(action: A): Job = coroutineScope.launch {
        _actions.send(action)
    }
}