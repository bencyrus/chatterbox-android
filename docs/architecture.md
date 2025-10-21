Status: current
Last verified: 2025-10-21

← Back to [`README.md`](../README.md)

## Architecture

### Why this exists

- Document the app’s structure and conventions to keep implementation clean and consistent.

### Role in the system

- Android Jetpack Compose client that talks to the gateway API using `HttpURLConnection` and stores tokens securely with EncryptedSharedPreferences.

### How it works

- Pattern: MVVM + Use Cases + Repository.
- Composables bind-only. Business logic in Use Cases and Repositories.
- Concurrency via coroutines/flows.

### Directories and key files

- `App/`
  - [`app/src/main/java/io/glovee/chatterbox/App/ChatterboxActivity.kt`](../app/src/main/java/io/glovee/chatterbox/App/ChatterboxActivity.kt)
- `Core/`
  - Config: [`Core/Config/AppEnvironment.kt`](../app/src/main/java/io/glovee/chatterbox/Core/Config/AppEnvironment.kt)
  - Networking: [`Core/Networking/APIClient.kt`](../app/src/main/java/io/glovee/chatterbox/Core/Networking/APIClient.kt)
  - Security: [`Core/Security/TokenManager.kt`](../app/src/main/java/io/glovee/chatterbox/Core/Security/TokenManager.kt)
  - Localization helpers: [`UI/Views/Strings.kt`](../app/src/main/java/io/glovee/chatterbox/UI/Views/Strings.kt)
- `Features/Auth/`
  - Models: [`Features/Auth/Models/AuthDTOs.kt`](../app/src/main/java/io/glovee/chatterbox/Features/Auth/Models/AuthDTOs.kt)
  - Repositories: [`Features/Auth/Repositories/AuthRepository.kt`](../app/src/main/java/io/glovee/chatterbox/Features/Auth/Repositories/AuthRepository.kt)
  - UseCases: [`Features/Auth/UseCases/AuthUseCases.kt`](../app/src/main/java/io/glovee/chatterbox/Features/Auth/UseCases/AuthUseCases.kt)
  - ViewModel: [`Features/Auth/ViewModel/AuthViewModel.kt`](../app/src/main/java/io/glovee/chatterbox/Features/Auth/ViewModel/AuthViewModel.kt)
- `UI/`
  - Views: [`UI/Views/LoginView.kt`](../app/src/main/java/io/glovee/chatterbox/UI/Views/LoginView.kt), [`UI/Views/HomeView.kt`](../app/src/main/java/io/glovee/chatterbox/UI/Views/HomeView.kt), [`UI/Views/SettingsView.kt`](../app/src/main/java/io/glovee/chatterbox/UI/Views/SettingsView.kt), [`UI/Views/RootTabView.kt`](../app/src/main/java/io/glovee/chatterbox/UI/Views/RootTabView.kt)
- `Resources/`
  - Localized strings: [`app/src/main/res/values/strings.xml`](../app/src/main/res/values/strings.xml)

### Configuration

- Centralized in `AppEnvironment` with base URL and endpoints. Keep secrets out of the repo.

### Operations

- Open the folder in Android Studio (Giraffe+), run on an Android 13/14 emulator or device.

### See also

- [`auth.md`](./auth.md)
