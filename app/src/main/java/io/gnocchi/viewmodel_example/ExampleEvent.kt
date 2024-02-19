package io.gnocchi.viewmodel_example

sealed class ExampleEvent {
    data object SavingData: ExampleEvent()
    data object DataSaved: ExampleEvent()
}