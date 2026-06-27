# Phase 3: Basic Gallery Grid & Thumbnail Loading

## Description
Display the imported images in a high-performance grid.

## Tasks
1.  **Application - GalleryService (Part 2):**
    *   Expose `Flow<List<Image>>` from the service.
2.  **Infrastructure - Coil Integration:**
    *   Implement a custom Coil `Fetcher`/`Decoder` if necessary to load images directly from private storage.
3.  **Presentation - Main Grid:**
    *   Create `GalleryScreen` using `LazyVerticalGrid`.
    *   Optimize for performance with large datasets (keying, stable IDs).

## Expected Output
*   A scrollable grid showing thumbnails of all imported images.
*   Smooth scrolling performance.
