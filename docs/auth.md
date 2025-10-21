Status: current
Last verified: 2025-10-21

← Back to [`README.md`](../README.md)

## Auth (Magic Token)

### Why this exists

- Provide a secure magic-link login; store tokens securely; refresh automatically.

### Role in the system

- Calls gateway endpoints to request and verify magic links. Manages access/refresh tokens.

### How it works

- Magic Token: Request link `POST /rpc/request_magic_link`; app opens via HTTPS App Link and exchanges via `POST /rpc/login_with_magic_token`.
- Tokens are returned in the login response body and may also be refreshed via gateway response headers; both are stored via `TokenManager`.

### Components

- API client: [`Core/Networking/APIClient.kt`](../app/src/main/java/io/glovee/chatterbox/Core/Networking/APIClient.kt)
- Token storage: [`Core/Security/TokenManager.kt`](../app/src/main/java/io/glovee/chatterbox/Core/Security/TokenManager.kt)
- Use cases: [`Features/Auth/UseCases/AuthUseCases.kt`](../app/src/main/java/io/glovee/chatterbox/Features/Auth/UseCases/AuthUseCases.kt)
- Repository: [`Features/Auth/Repositories/AuthRepository.kt`](../app/src/main/java/io/glovee/chatterbox/Features/Auth/Repositories/AuthRepository.kt)
- View model: [`Features/Auth/ViewModel/AuthViewModel.kt`](../app/src/main/java/io/glovee/chatterbox/Features/Auth/ViewModel/AuthViewModel.kt)
- UI: [`UI/Views/LoginView.kt`](../app/src/main/java/io/glovee/chatterbox/UI/Views/LoginView.kt)

### UX notes

- Validate identifiers; show clear error messages.
- Magic links open the app and log in automatically.

### App Links

- App Link: `https://<your-domain>/auth/magic?token=<token>` (autoVerify in Manifest).

### Configuration

- strings.xml:
  - `api_base_url` (String): e.g., https://api.glovee.io
  - `universal_link_hosts` (String, comma-separated): e.g., glovee.io
  - `magic_link_path` (String): e.g., /auth/magic
- Manifest Intent Filter: autoVerify VIEW with `https`, host(s), and path prefix.
- Server config via secrets (Postgres migrations):
  - `MAGIC_LOGIN_LINK_HTTPS_BASE_URL=https://<your-domain>/auth/magic`

### See also

- [`architecture.md`](./architecture.md)
