# Architecture Document - LuminaGallery

This document describes the implementation-level architecture of LuminaGallery, based on a practical layered approach that prioritizes data privacy and reactive UI updates.

## 1. High-Level Architecture

LuminaGallery follows a **Layered Architecture** pattern, built iteratively. We focus on defining core data models first, then building application logic, and abstracting infrastructure details as needed.

### Layers:
1.  **Data Model Layer (Core):** Defines the fundamental data structures (Images, Tags, SavedCriteria).
2.  **Application Layer (Services):** Contains the business logic and orchestrates how the app functions. This layer calls into the Data Model and Infrastructure layers.
3.  **Infrastructure Layer (Implementation):** Concrete implementations for platform-specific tasks (Database access, File System operations, Biometrics, Networking). These are often defined by interfaces in the Application layer when abstraction is needed.
4.  **Presentation Layer (UI):** Jetpack Compose screens and ViewModels that interact with the Application layer.

### Development Workflow:
*   **Model First:** Define the data entities and relationships.
*   **Application Logic:** Implement feature-specific logic in Services.
*   **Infrastructure Abstraction:** If a service needs to perform an operation like "save to disk" or "query database", we define the requirement and implement it in the Infrastructure layer.
*   **Iterative Feature Delivery:** Features are built one-by-one, completing all layers for a single feature before moving to the next.

---

## 2. Detailed Layer Analysis

### 2.1 Data Model Layer
The foundation of the application.
*   **Image:** Represents a private photo metadata, including paths to both the original high-resolution file and a pre-generated thumbnail for the grid view.
*   **Tag:** Represents a user-defined category.
*   **SavedCriteria:** Defines search/filter parameters.

### 2.2 Application Layer (Services)
Provides the API for the UI.
*   **GalleryService:** Manages image listing, filtering, and basic lifecycle (import/delete).
*   **TaggingService:** Handles the logic of creating, applying, and removing tags.
*   **AuthenticationService:** Manages the logic flow for user identity and biometric triggers.

### 2.3 Infrastructure Layer
Implements the "how" for the Application layer.
*   **Persistence (Room):** Implementation of data storage and retrieval.
*   **Storage (FileVault):** Handles the physical moving and securing of image files in `filesDir`, as well as the generation and storage of thumbnails to optimize performance in the gallery grid.
*   **Platform Services:** Biometric prompt implementations, FileProviders for sharing.