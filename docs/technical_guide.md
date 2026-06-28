# Technical Guide - LuminaGallery

This document outlines the technical standards and libraries used in LuminaGallery.

## 1. Core Technologies & Libraries

### Language: Kotlin
- Use modern Kotlin features (Coroutines, Flow, Serialization).
- Prefer `val` over `var`.
- Use `internal` visibility for module-private components.

### UI: Jetpack Compose
- Follow Material Design 3 (M3) guidelines.
- Use Unidirectional Data Flow (UDF) pattern.
- Composable functions should be stateless where possible.

### Dependency Injection: Hilt
- Use Hilt for all dependency injection.
- Define modules for providing Infrastructure and Application layer components.
- Use `@Inject` for constructor injection.

### Asynchronous Programming: Coroutines & Flow
- Perform disk I/O and database operations on `Dispatchers.IO`.
- Use `StateFlow` for UI state in ViewModels.
- Avoid `GlobalScope`; use `viewModelScope` or `lifecycleScope`.

### Persistence: Room
- Use Room for local database storage.
- Define DAOs for clear data access patterns.
- Use `Transaction` for atomic multi-step operations.

### Networking: Ktor
- Prepared for future backend synchronization.
- Use Kotlinx Serialization for JSON parsing.

### Image Loading: Coil & Telephoto
- **Coil:** Standard image loading and caching.
- **Telephoto (`ZoomableAsyncImage`):** High-performance zooming and sub-sampling for large images.

---

## 2. Development Standards

### Coding Hygiene
- Always remove unused imports and statements.
- Avoid deprecated APIs; justify their use if necessary.
- Document complex algorithms or non-obvious business logic.

### Testing
- Write unit tests for Application services and Infrastructure components.
- Use Robolectric for database and Android-specific logic testing where applicable.
- Aim for high coverage of business logic in the Application layer.
