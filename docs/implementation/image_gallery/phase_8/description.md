# Phase 8: External Integration

## Description
Allow LuminaGallery to interact with the wider Android ecosystem securely.

## Tasks
1.  **Infrastructure - FileProvider:**
    *   Configure `FileProvider` to share files from private storage.
2.  **External Intents (Inbound):**
    *   Handle `ACTION_GET_CONTENT` and `ACTION_PICK` in `MainActivity`.
    *   Ensure biometric check is performed before allowing a selection.
3.  **External Intents (Outbound):**
    *   Add a "Share" button to the detail screen using `Intent.ACTION_SEND`.

## Expected Output
*   LuminaGallery appears as a source when other apps request a photo.
*   Users can share individual private photos to other apps.
