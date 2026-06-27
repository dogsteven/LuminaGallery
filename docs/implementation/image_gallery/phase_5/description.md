# Phase 5: Tagging System

## Description
Implement categorization through custom tags.

## Tasks
1.  **Application - TaggingService:**
    *   Logic for creating, deleting, and assigning tags to images.
2.  **Infrastructure - Persistence (Tags):**
    *   Ensure Room handles cascading deletes for tags (removing associations).
3.  **Presentation - Tag Management UI:**
    *   UI to create/delete tags.
    *   Interface in the detail screen to add/remove tags from a specific image.

## Expected Output
*   Users can categorize images with tags.
*   Deleting a tag cleans up all image associations.
