# Phase 1: Project Skeleton & Data Models

## Description
This phase focuses on setting up the core data structures and the persistence layer. We will define the entities for our layered architecture and set up Room database.

## Tasks
1.  **Define Data Models:**
    *   `ImageEntity`: ID, internal path, thumbnail path, description, timestamp.
    *   `TagEntity`: ID, name.
    *   `ImageTagCrossRef`: Many-to-many relationship between Images and Tags.
    *   `SavedCriteriaEntity`: For storing search filters.
2.  **Infrastructure - Persistence:**
    *   Set up Room Database (`LuminaDatabase`).
    *   Create DAOs for Images, Tags, and Relationships.
3.  **Hilt Setup:**
    *   Provide Database and DAO instances via Hilt modules.

## Expected Output
*   Compilable project with Room entities and DAOs.
*   Hilt dependency injection configured for the data layer.
*   Unit tests for DAO operations.
