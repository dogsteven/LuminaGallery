# Phase 6: Advanced Filtering & Saved Criteria

## Description
Enable users to find images quickly using multiple criteria.

## Tasks
1.  **Application - Filter Logic:**
    *   Update `GalleryService` to accept `SavedCriteria` for its image flow.
2.  **Infrastructure - Persistence (Filters):**
    *   Implement saving and loading `SavedCriteria` entities.
3.  **Presentation - Filter UI:**
    *   A filter drawer or sheet to select tags, dates, and search text.
    *   Ability to save the current filter setup.

## Expected Output
*   Dynamic filtering of the main grid.
*   Users can save and reload their favorite searches.
*   **Refinement:** Robust image deletion that cleans up both database entries and physical files.
