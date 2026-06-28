# Phase 5: Tagging System

## Description
Implement categorization through custom tags.

## Technical Scope / Expected Deliverables
1.  **Application - TaggingService:**
    *   Business logic for tag lifecycle and image-tag associations.
2.  **Infrastructure - Persistence (Tags):**
    *   Cascading delete logic in Room for automated cleanup.
3.  **Presentation - Tag Management UI:**
    *   Global tag management interface and per-image tagging BottomSheet.

## Expected Output
*   Users can categorize images with tags.
*   Deleting a tag cleans up all image associations.
