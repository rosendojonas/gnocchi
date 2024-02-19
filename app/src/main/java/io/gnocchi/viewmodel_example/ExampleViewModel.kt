package io.gnocchi.viewmodel_example

import io.gnocchi.GnocchiViewModel

class ExampleViewModel(override val initState: ExampleState) : GnocchiViewModel<ExampleAction, ExampleState, ExampleEvent>() {

    override fun processAction(action: ExampleAction) {
        when (action) {
            ExampleAction.Load -> TODO()
            is ExampleAction.LoadDataById -> TODO()
        }
    }
}

