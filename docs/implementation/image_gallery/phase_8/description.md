# Phase 8: External Integration

## Description
Allow LuminaGallery to interact with the wider Android ecosystem securely.

## Technical Scope / Expected Deliverables
1.  **Infrastructure - FileProvider:**
    *   Secure URI generation for sharing files from internal private storage.
2.  **External Intents (Inbound):**
    *   `ACTION_GET_CONTENT` and `ACTION_PICK` handling with integrated biometric gating.
3.  **External Intents (Outbound):**
    *   `ACTION_SEND` integration for outbound sharing of individual images.

## Expected Output
*   LuminaGallery appears as a source when other apps request a photo.
*   Users can share individual private photos to other apps.
