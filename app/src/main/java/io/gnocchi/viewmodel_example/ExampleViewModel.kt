package io.gnocchi.viewmodel_example

import androidx.lifecycle.viewModelScope
import io.gnocchi.GnocchiViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ExampleViewModel: GnocchiViewModel<ExampleAction, ExampleState, ExampleEvent>(ExampleState.Empty) {

    override fun processAction(action: ExampleAction) {
        when (action) {
            ExampleAction.Load -> load()
            is ExampleAction.LoadDataById -> loadById(action)
        }
    }

    private fun loadById(action: ExampleAction.LoadDataById) {
        updateState(ExampleState.Loading)
        viewModelScope.launch {
            delay(2000)
            updateState(ExampleState.DataLoaded(listOf("${action.id}")))
        }
    }

    private fun load() {
        updateState(ExampleState.Loading)
        viewModelScope.launch {
            delay(2000)
            updateState(ExampleState.DataLoaded(listOf("a", "b", "c")))
        }
    }
}

