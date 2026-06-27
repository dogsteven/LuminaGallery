# Phase 2: Private Storage & Import Mechanism

## Description
This phase implements the logic to move images from the public device storage into the application's private `filesDir`.

## Tasks
1.  **Infrastructure - FileVault & Thumbnails:**
    *   Create `FileVaultManager` to handle file operations (copying from Uri, deleting files, getting internal file references).
    *   Implement thumbnail generation logic (downscaling images) to be stored alongside the original files.
2.  **Application - GalleryService (Part 1):**
    *   Implement `importImage(uri: Uri)`: Use `FileVaultManager` to save the original file, generate a thumbnail, and use Room to save the metadata (including both paths).
3.  **Basic UI for Import:**
    *   A simple button in `MainActivity` to launch the system photo picker and trigger the import.

## Expected Output
*   Images can be selected from the system and copied to the app's private storage.
*   Database records are created for each imported image.
*   Verification that imported images are NOT visible in the system gallery.
