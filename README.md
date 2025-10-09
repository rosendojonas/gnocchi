# Gnocchi

[![JitPack](https://img.shields.io/jitpack/v/github/rosendojonas/gnocchi)](https://jitpack.io/#rosendojonas/gnocchi)
![Kotlin](https://img.shields.io/badge/Kotlin-2.2.x-blue)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-Compatible-success)
![License](https://img.shields.io/badge/License-Apache%202.0-green)

Utilitários de arquitetura para Android focados em um fluxo **Ação → Estado → Evento** (um MVI simples) com **Kotlin Coroutines/Flow**, integração para **Jetpack Compose** e também para **Views/Fragments** tradicionais.

Este repositório contém três módulos:

- **`gnocchi-core`**: `GnocchiViewModel` (base genérica para ACTION/STATE/EVENT), canais de ações, `StateFlow` de estado e `Flow` de eventos.
- **`gnocchi-compose`**: extensões para Compose (`rememberGnocchiState`, `ObserveEvents`).
- **`gnocchi-android-view`**: extensões para Fragments/Activities com Views (`observeStates`, `observeEvents`).

> SDKs e metadados (de `buildSrc/BuildConfiguration.kt`):
> - `compileSdk = 36`, `targetSdk = 36`, `minSdk = 21`
> - Grupo/artefatos: `com.github.rosendojonas.gnocchi`
> - Versão exemplo: **`0.1.23`**
> - Licença: **Apache-2.0**

---

## Índice

- [Por que usar](#por-que-usar)
- [Arquitetura em 1 minuto](#arquitetura-em-1-minuto)
- [Instalação](#instalação)
  - [Gradle (Kotlin DSL)](#gradle-kotlin-dsl)
  - [Dependências por módulo](#dependências-por-módulo)
- [Como usar](#como-usar)
  - [1) Defina Action/State/Event](#1-defina-actionstateevent)
  - [2) Crie seu ViewModel](#2-crie-seu-viewmodel)
  - [3) Consuma no Jetpack Compose](#3-consuma-no-jetpack-compose)
  - [4) Consuma em Fragments/Activities (Views)](#4-consuma-em-fragmentsactivities-views)
- [API essencial](#api-essencial)
- [Exemplo completo](#exemplo-completo)
- [Proguard/R8](#proguardr8)
- [Compatibilidade](#compatibilidade)
- [FAQ](#faq)
- [Roadmap](#roadmap)
- [Contribuindo](#contribuindo)
- [Licença](#licença)

---

## Por que usar

- **Padroniza** a forma de reagir a ações do usuário e efeitos de UI.
- **Separa** claramente *estado durável* de *eventos efêmeros*.
- **Funciona nos dois mundos**: Compose e a pilha tradicional de Views.
- **Leve**: utiliza Coroutines/Flow e uma pequena camada de extensões.

---

## Arquitetura em 1 minuto

No `GnocchiViewModel<ACTION, STATE, EVENT>`:

- Você envia **ações** via `viewModel.action(ACTION)`.
- O ViewModel **processa** cada ação em `processAction` consumindo um `Channel<ACTION>`.
- O **estado atual** fica em `StateFlow<STATE>` (imutável para a UI).
- **Eventos** pontuais (snackbars, navegação, toasts…) saem por `Flow<EVENT>`.

Compose consome `StateFlow` com `rememberGnocchiState` e **observa** `EVENT`s com `ObserveEvents`.
Em Views, use `observeStates` e `observeEvents` em `Fragment`/`ComponentActivity`.

---

## Instalação

### Gradle (Kotlin DSL)

No **`settings.gradle.kts`**, garanta o repositório do JitPack:

```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://jitpack.io")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}
```

### Dependências por módulo

> Grupo: `com.github.rosendojonas.gnocchi`  
> Versão: **`0.1.23`**

```kotlin
dependencies {
    // Core (ViewModel base, ações/estado/eventos)
    implementation("com.github.rosendojonas.gnocchi:gnocchi-core:0.1.23")

    // Integrações Jetpack Compose
    implementation("com.github.rosendojonas.gnocchi:gnocchi-compose:0.1.23")

    // Integrações para Views/Fragments
    implementation("com.github.rosendojonas.gnocchi:gnocchi-android-view:0.1.23")
}
```

> **Dica:** Use uma propriedade de versão centralizada em `libs.versions.toml` para facilitar upgrades.

---

## Como usar

### 1) Defina Action/State/Event

```kotlin
sealed class ExampleAction {
    data object Load : ExampleAction()
    data class LoadDataById(val id: Int) : ExampleAction()
}

sealed class ExampleState {
    data object Loading : ExampleState()
    data class DataLoaded(val data: List<String>) : ExampleState()
    data object Empty : ExampleState()
}

sealed class ExampleEvent {
    data object SavingData : ExampleEvent()
    data object DataSaved : ExampleEvent()
}
```

### 2) Crie seu ViewModel

```kotlin
class ExampleViewModel
    : GnocchiViewModel<ExampleAction, ExampleState, ExampleEvent>(ExampleState.Empty) {

    override fun processAction(action: ExampleAction) {
        when (action) {
            ExampleAction.Load -> load()
            is ExampleAction.LoadDataById -> loadById(action.id)
        }
    }

    private fun loadById(id: Int) {
        updateState(ExampleState.Loading)
        viewModelScope.launch {
            delay(2000)
            updateState(ExampleState.DataLoaded(listOf("$id")))
            sendEvent(ExampleEvent.DataSaved)
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
```

### 3) Consuma no Jetpack Compose

```kotlin
@Composable
fun ExampleScreen(viewModel: ExampleViewModel = remember { ExampleViewModel() }) {
    val state = rememberGnocchiState(viewModel.state)

    ObserveEvents(events = viewModel.events) { event ->
        when (event) {
            ExampleEvent.SavingData -> { /* mostrar progresso */ }
            ExampleEvent.DataSaved  -> { /* snackbar/navegação */ }
        }
    }

    when (val s = state.value) {
        ExampleState.Loading -> CircularProgressIndicator()
        is ExampleState.DataLoaded -> Text("Itens: ${s.data.joinToString()}")
        ExampleState.Empty -> Text("Nada ainda. Toque para carregar.")
    }
}
```

APIs de Compose (`gnocchi-compose`):

- `rememberGnocchiState(stateFlow: StateFlow<T>): State<T>`
- `ObserveEvents(events: Flow<EVENT>, onEvent: (EVENT) -> Unit)`

### 4) Consuma em Fragments/Activities (Views)

```kotlin
class ExampleFragment : Fragment(R.layout.example_fragment) {

    private val viewModel by viewModels<ExampleViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observeStates(viewModel.state) { state ->
            // Renderize o estado
        }

        observeEvents(viewModel.events) { event ->
            // Trate eventos
        }

        viewModel.action(ExampleAction.Load)
    }
}
```

APIs de Views (`gnocchi-android-view`):

- `Fragment.observeStates(stateFlow, onChanged)`
- `Fragment.observeEvents(events, onEvent)`
- `ComponentActivity.observeStates(...)`
- `ComponentActivity.observeEvents(...)`

---

## API essencial

### `GnocchiViewModel<ACTION, STATE, EVENT>(initState: STATE)`
- **Propriedades**
  - `val state: StateFlow<STATE>` — estado atual da UI (imutável para a camada de visualização).
  - `val events: Flow<EVENT>` — eventos one-shot para efeitos (snackbar, navegação…).
- **Entrada**
  - `fun action(action: ACTION): Job` — envia uma ação para processamento.
- **Implementação**
  - `abstract fun processAction(action: ACTION)` — *override obrigatório* para tratar cada ação.
  - `protected open fun updateState(newState: STATE)` — atualiza o estado.
  - `protected fun sendEvent(event: EVENT): Job` — emite um evento efêmero.
- **Internals**
  - Canal de ações (`Channel<ACTION>`) consumido no `CoroutineScope(Dispatchers.Main)`.

### `gnocchi-compose`
- `rememberGnocchiState(stateFlow)`
- `ObserveEvents(events, onEvent)`

### `gnocchi-android-view`
- `Fragment.observeStates(...)`, `Fragment.observeEvents(...)`
- `ComponentActivity.observeStates(...)`, `ComponentActivity.observeEvents(...)`

---

## Exemplo completo

Um exemplo funcional já está no módulo **`app`** do projeto:

- `app/src/main/java/io/gnocchi/viewmodel_example/ExampleAction.kt`
- `app/src/main/java/io/gnocchi/viewmodel_example/ExampleState.kt`
- `app/src/main/java/io/gnocchi/viewmodel_example/ExampleEvent.kt`
- `app/src/main/java/io/gnocchi/viewmodel_example/ExampleViewModel.kt`
- `app/src/main/java/io/gnocchi/MainActivity.kt` (uso com Compose: `rememberGnocchiState`)

---

## Proguard/R8

- Não há requisitos especiais além dos já convencionais de Android/Kotlin/Coroutines.
- Caso utilize **reflexão**, mantenha suas classes de `Action/State/Event` fora da ofuscação.

  ---

## Proguard/R8 (guia prático para consumidores da lib)

A **Gnocchi não usa reflexão** para acessar suas `Action/State/Event`, então **não há keep-rules obrigatórias** impostas pela lib.  
Você **só precisa** de regras se **o seu app** usar reflexão (ou libs que usam) sobre **as suas próprias** classes de `Action/State/Event`.

### Quando eu preciso de regras?
- Você usa **Gson/Moshi/Jackson sem codegen** para (de)serializar `Action/State/Event`.
- Você usa **reflexão manual** (`Class.forName`, `KClass.members`, etc.) nessas classes.
- Seu DI ou alguma lib de runtime **inspeciona** essas classes por reflexão.

> Se você usa **kotlinx.serialization** ou **Moshi com codegen**, na maioria dos casos **não precisa** de regras extras para suas `Action/State/Event`.

---

### Abordagens recomendadas

**Opção A — Anotar com `@Keep` (direto e à prova de esquecimento)**  
```kotlin
import androidx.annotation.Keep

@Keep
sealed class ExampleAction { /* ... */ }

@Keep
sealed class ExampleState { /* ... */ }

@Keep
sealed class ExampleEvent { /* ... */ }
```

**Opção B — Regras `-keep` por pacote (centralizado em ProGuard/R8)**  
```proguard
# Mantenha as classes sob reflexão (ajuste os pacotes para o seu app)
-keep class com.seuapp.ui.example.action.** { *; }
-keep class com.seuapp.ui.example.state.**  { *; }
-keep class com.seuapp.ui.example.event.**  { *; }
```

---

### Casos comuns (com exemplos)

#### 1) **Gson** (modo reflexivo)
Gson usa reflexão por padrão. Garanta que as classes alvo **não** sejam ofuscadas/removidas.
```proguard
# Gson em si
-dontwarn sun.misc.**

# Suas classes de Action/State/Event sob reflexão
-keep class com.seuapp.ui.** { *; }
```
> Alternativa: use DTOs próprios para rede e mantenha `Action/State/Event` fora de (de)serialização.

#### 2) **Moshi**
- **Com codegen** (`@JsonClass(generateAdapter = true)`): normalmente **dispensa** keeps extras para suas classes.
- **Sem codegen** (modo reflexivo): trate como Gson.
```proguard
# Moshi (geralmente suficiente; ajuste conforme seu projeto)
-keep class com.squareup.moshi.** { *; }
-dontwarn com.squareup.moshi.**
# Se estiver reflexivo sobre suas classes:
-keep class com.seuapp.ui.** { *; }
```

#### 3) **kotlinx.serialization**
Com codegen padrão:
```proguard
# Geralmente opcional; adicione se seu build reclamar
-keep class kotlinx.serialization.** { *; }
-keepattributes *Annotation*
```
> Em cenários típicos, nenhuma regra adicional é necessária para suas `Action/State/Event`.

#### 4) **Reflexão manual / Navegação por eventos**
Se você faz `when (event)` baseado em nomes ou usa reflexão em tempo de execução:
```proguard
-keep class com.seuapp.ui.example.event.** { *; }
```

#### 5) **DI com reflexão (ex.: alguns containers customizados)**
Mantenha apenas o que o container inspeciona (tipos/constructors anotados):
```proguard
-keepclasseswithmembers class com.seuapp.** {
    @Inject <init>(...);
}
-keep class com.seuapp.di.** { *; }
```

---

### Dicas de depuração
- Rode um *release build* com `minifyEnabled true` e ative **mapping**. Se algo “sumir”, procure pelo nome ofuscado no `mapping.txt`.
- Teste flows de (de)serialização e emissão de eventos/estados em um APK **minificado** (CI ajuda!).
- Prefira **codegen** a reflexão quando possível (Moshi codegen, kotlinx.serialization).

---

### E na própria lib (Gnocchi)?
A Gnocchi inclui apenas um `consumer-rules.pro` **mínimo** (ou vazio). Não há regras especiais para `Action/State/Event` dos apps consumidores.  
Se quiser replicar no seu módulo lib:
```kotlin
android {
  defaultConfig {
    consumerProguardFiles("consumer-rules.pro")
  }
}
```

---

## Compatibilidade

- **MinSdk**: 21+  
- **Kotlin**: 2.2.x  
- **Compose**: compatível com BOM recente (vide `libs.versions.toml`)  
- **Coroutines**: 1.10.x

---

## FAQ

**1) Por que separar `State` de `Event`?**  
Estados são *duráveis* e precisam sobreviver a recomposições ou recriações de tela. Eventos são *efêmeros* (ex.: mostrar Snackbar) e não devem ser reemitidos após rotação/recriação.

**2) Posso usar LiveData?**  
O core usa `StateFlow`/`Flow`. Se precisar, converta (`asLiveData()`) na camada de apresentação, mas o recomendado é manter Flow.

**3) Como testar?**  
Use `Turbine` ou coletores de Flow com `runTest`. Valide a sequência de estados e a emissão de eventos.

**4) Como lidar com navegação?**  
Modele navegação como `Event` (ex.: `NavigateToDetails(id)`) e consuma na camada de UI. Ou exponha um `Flow<NavEvent>` separado.

**5) O que fazer com erros?**  
Inclua no seu `State` (ex.: `Error(message)`) e/ou emita `Event` para mostrar mensagens. Evite lançar exceções sem tratamento em `processAction`.

---

## Contribuindo

Contribuições são muito bem-vindas!

1. Abra uma *issue* descrevendo a motivação.
2. Crie um *branch* com sua mudança e adicione testes/exemplos quando fizer sentido.
3. Siga o estilo dos módulos e mantenha as dependências enxutas.

---

## Licença

Este projeto é licenciado sob a **Apache License 2.0**. Consulte o arquivo `LICENSE`.

---
