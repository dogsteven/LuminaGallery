# Phase 6: Advanced Filtering & Saved Criteria

## Description
Enable users to find images quickly using multiple criteria.

## Technical Scope / Expected Deliverables
1.  **Application - Filter Logic:**
    *   Reactive filtering logic in `GalleryService` using `SavedCriteria`.
2.  **Infrastructure - Persistence (Filters):**
    *   Persistence layer for `SavedCriteria` entities.
3.  **Presentation - Filter UI:**
    *   Multi-criteria filter interface and saved criteria management.
4.  **Robust Deletion:**
    *   Cleanup logic for both database entries and physical storage upon image deletion.

## Expected Output
*   Dynamic filtering of the main grid.
*   Users can save and reload their favorite searches.
*   **Refinement:** Robust image deletion that cleans up both database entries and physical files.
