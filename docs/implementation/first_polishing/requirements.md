# Epic Requirements: First Polishing

## 1. Introduction
This document defines the functional and non-functional requirements for the **First Polishing** epic of LuminaGallery. The goal of this epic is to refine the core data models for future external system integration and to enhance the user experience and visual polish of the application.

**Status:** ⏳ Pending

## 2. Functional Requirements

### 2.1 Data Model Refinement
*   **Composite Primary Key (Images):** Transition `ImageEntity` to use a composite primary key consisting of `source` (String) and `identifier` (UUID).
*   **Name-based Primary Key (Tags):** Transition `TagEntity` to use `name` (String) as its primary key.
*   **Code-based Primary Key (Saved Criteria):** Transition `SavedCriteriaEntity` to use `code` (String) as its primary key.
*   **Migration Strategy:** Implement a destructive migration strategy that drops and recreates all tables, as there is no production data to preserve.

### 2.2 Gallery UX Enhancements
*   **Swipe-to-Dismiss:** Implement a hard swipe-down gesture in the image detail view to return to the gallery grid.
*   **Unified Content Sheet:** Replace the `TagContentSheet` with a `ContentSheet` that displays tags, description, creation date, source, and identifier.
*   **Relocated Deletion:** Move the image deletion action from the detail view top bar into the new `ContentSheet`.
*   **Image Rotation:** Provide a 90-degree rotation toggle in the detail view for viewing convenience.

### 2.3 Visual Polish & Navigation
*   **Navigation Migration:** Replace the existing bottom navigation bar with a navigation drawer (side drawer) to support a larger number of future screens and features.
*   **Top Navigation Bar:** Implement a Material 3 `TopAppBar` that integrates with the side drawer, providing screen context and consistent navigation across the app.
*   **Sectioned Navigation (Late Request):** Categorize navigation drawer items into distinct sections (e.g., "Main", "Tools") to provide visual hierarchy and prepare for future off-topic features.

### 2.4 Security & Privacy
*   **App Switcher Privacy:** Implement `FLAG_SECURE` to ensure the application displays a black screen in the recent apps switcher and prevents screen captures.

## 3. Non-Functional Requirements

### 3.1 Data Integrity
*   Enforce uniqueness of tag names and saved criteria codes at the database level.
*   Ensure the new composite key structure correctly identifies images across different potential sources.

### 3.2 Usability
*   The swipe-to-dismiss gesture should feel natural and responsive, mimicking standard gallery application behavior.
*   The `ContentSheet` should be easily accessible and organize information clearly according to Material 3 standards.
