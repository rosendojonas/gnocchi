package io.gnocchi.viewmodel_example

sealed class ExampleAction {
    data object Load: ExampleAction()
    data class LoadDataById(val id: Int): ExampleAction()
}