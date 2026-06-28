# LuminaGallery - Private Image Gallery

LuminaGallery is a private, personal image gallery application designed for secure local storage and efficient browsing of sensitive images. It is built with a focus on privacy and user-controlled organization.

## Project Overview

- **Name:** LuminaGallery
- **Goal:** Provide a secure, high-performance image gallery that doesn't share data with other applications.
- **Primary Focus:** Privacy (Biometric Auth) and efficient management of large image collections.

## Tech Stack

- **UI:** Jetpack Compose (Modern declarative UI)
- **Dependency Injection:** Hilt
- **Networking:** Ktor (Prepared for future backend synchronization)
- **Serialization:** Kotlinx Serialization
- **Architecture:** Layered Architecture (Data Model -> Application -> Infrastructure -> Presentation)
- **Language:** Kotlin
- **Minimum SDK:** 30 (Android 11)
- **Target SDK:** 36 (Android 15+)

## Operating Rules

- **Git Management:** Without being explicitly requested, you are strictly forbidden from manipulating git (e.g., commit, stage, reset, etc.).
- **Code Hygiene:** Always check for and remove unused statements and imports. Avoid using deprecated APIs unless absolutely necessary, and justify their use if they are used.
- **Documentation:** Always consult the `docs/` directory for detailed standards and historical context.

## Development Context for AI Agents

- **Entry Point:** Start by reviewing the [Documentation Index](file:///Users/rio/AndroidStudioProjects/LuminaGallery/docs/README.md).
- **Architecture & Standards:** Refer to the [Architecture Guide](file:///Users/rio/AndroidStudioProjects/LuminaGallery/docs/architecture_guide.md) and [Technical Guide](file:///Users/rio/AndroidStudioProjects/LuminaGallery/docs/technical_guide.md).
- **Feature History:** See the [Epic Journal](file:///Users/rio/AndroidStudioProjects/LuminaGallery/docs/implementation/image_gallery/journal.md) and [Epic Summary](file:///Users/rio/AndroidStudioProjects/LuminaGallery/docs/implementation/image_gallery/summary.md) for the "Private Image Gallery Core" epic.
- **Current State:** The application is a single-module project (`:app`). ViewModels for core features are Activity-scoped to ensure state durability.
- **Security:** Always prioritize data isolation. Images are stored in `filesDir` and shared only via `FileProvider`.

## Documentation Structure Guild

1.  **Architecture:** `docs/architecture_guide.md` defines the layers and workflow.
2.  **Technical:** `docs/technical_guide.md` defines libraries and coding standards.
3.  **ADRs:** `docs/adr/` for significant architectural decisions.
4.  **Journals:** `docs/journal/` for system-level history; `docs/implementation/<epic>/journal.md` for epic-level history.
5.  **Implementation:** Each Epic has its own directory in `docs/implementation/`, containing requirements, user stories, journals, summaries, and phase-specific deliverables.
