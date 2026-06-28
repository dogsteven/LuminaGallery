# Phase 1: Data Model Refactoring & Migration

## Description
This phase focuses on transitioning the persistence layer to the new identification system. This is a breaking change that will utilize a destructive migration strategy.

## Technical Scope / Expected Deliverables
1.  **Data Model Updates (Core):**
    *   `ImageEntity`: Update primary key to composite `(source, identifier)`.
    *   `TagEntity`: Update primary key to `name`.
    *   `SavedCriteriaEntity`: Update primary key to `code`.
2.  **Infrastructure - Persistence:**
    *   Update DAOs to reflect new primary key types and relationships.
    *   Implement `fallbackToDestructiveMigration()` in `LuminaDatabase`.
3.  **Application Layer Alignment:**
    *   Refactor `GalleryService`, `TagService`, and related components to use the new identification structures.

## Expected Output
*   Data layer updated with the new identification schema.
*   Application layer successfully refactored to handle composite and string-based keys.
*   Clean database state upon first run after update.
