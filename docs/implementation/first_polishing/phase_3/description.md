# Phase 3: Unified Content Management

## Description
This phase centralizes image metadata and management actions into a single, comprehensive interface within the gallery detail view.

## Technical Scope / Expected Deliverables
1.  **Unified Content Sheet (Presentation):**
    - Create `ContentSheet` replacing the old `TagContentSheet`.
    - Display all metadata: Tags, Description, Creation Date, Source, and Identifier.
2.  **Action Relocation (Presentation/Application):**
    - Move "Delete" functionality from the top bar to the `ContentSheet`.
    - Update `GalleryViewModel` and `ImageDetailViewModel` to coordinate metadata updates and deletion via the new sheet.

## Expected Output
*   Clean detail view with a unified information sheet.
*   Deletion action securely relocated to avoid accidental triggers.
*   Full visibility of image provenance (Source/ID) and metadata.
