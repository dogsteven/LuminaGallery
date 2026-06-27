# LuminaGallery - Private Image Gallery

LuminaGallery is a private, personal image gallery application designed for secure local storage and efficient browsing of sensitive images. It is built with a focus on privacy and user-controlled organization.

## Project Overview

- **Name:** LuminaGallery
- **Goal:** Provide a secure, high-performance image gallery that doesn't share data with other applications.
- **Primary Focus:** Privacy (Biometric Auth) and efficient management of large image collections.

## Key Features

1.  **Main Image Grid:**
    - High-performance grid display for large image sets.
    - Advanced filtering by description, creation date, and other metadata.
    - Full-screen image viewing with smooth zoom (pinch-to-zoom) capabilities.
2.  **Tagging System:**
    - Create and delete custom tags.
    - Tag/untag images to enable efficient categorization.
    - Filter the main grid by one or multiple tags.
    - Automatic untagging of images when a tag is deleted.
3.  **Saved Filters:**
    - Ability to save complex filter configurations for quick access in the future.
4.  **Image Import & Sharing:**
    - Import from local device storage (file picker) and system gallery.
    - **Share on Demand:** Support for providing images to other apps via `ACTION_GET_CONTENT` or `ACTION_PICK` requests. This allows the app to serve as an image source while keeping the collection private.
    - (Future) Synchronization with a local backend server.
5.  **Privacy & Security:**
    - **Biometric Authorization:** Mandatory biometric check (Fingerprint/Face) to enter the application, including when launched from another app for "on demand" sharing.
    - Private storage: Images are managed within the app's private scope to prevent exposure to other apps.

## Tech Stack

- **UI:** Jetpack Compose (Modern declarative UI)
- **Dependency Injection:** Hilt
- **Networking:** Ktor (Prepared for future backend synchronization)
- **Serialization:** Kotlinx Serialization
- **Architecture:** Layered Architecture (Data Model -> Application -> Infrastructure -> Presentation)
- **Language:** Kotlin
- **Minimum SDK:** 30 (Android 11)
- **Target SDK:** 36 (Android 15+)

## Development Context for AI Agents

- The application is a single-module project (`:app`).
- The `MainActivity` serves as the entry point, likely handling the biometric authentication flow before navigating to the gallery.
- Use Hilt for all dependency injections.
- Prefer Compose-based UI components.
- When implementing filters, ensure they are optimized for performance against large datasets.
- Always prioritize security and data isolation.
