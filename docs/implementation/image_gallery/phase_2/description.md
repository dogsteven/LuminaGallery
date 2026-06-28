# Phase 2: Private Storage & Import Mechanism

## Description
This phase implements the logic to move images from the public device storage into the application's private `filesDir`.

## Technical Scope / Expected Deliverables
1.  **Infrastructure - FileVault & Thumbnails:**
    *   `FileVaultManager` for secure file handling and downscaling thumbnails.
2.  **Application - GalleryService (Part 1):**
    *   `importImage(uri: Uri)` logic: Save original, generate thumbnail, save Room metadata.
3.  **Basic UI for Import:**
    *   System photo picker integration in `MainActivity` to trigger imports.

## Expected Output
*   Images can be selected from the system and copied to the app's private storage.
*   Database records are created for each imported image.
*   Verification that imported images are NOT visible in the system gallery.
