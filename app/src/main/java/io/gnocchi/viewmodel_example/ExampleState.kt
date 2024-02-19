package io.gnocchi.viewmodel_example

sealed class ExampleState {
    data object Loading : ExampleState()
    data class DataLoaded(val data: List<String>): ExampleState()
}