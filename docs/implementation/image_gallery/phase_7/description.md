# Phase 7: Biometric Security & App Lock

## Description
Protect the gallery with biometric and device credential authentication using a UI-only overlay to maintain business logic isolation.

## Tasks
1.  **Infrastructure - BiometricManager:**
    *   Wrapper for `BiometricPrompt` supporting both `BIOMETRIC_STRONG` and `DEVICE_CREDENTIAL`.
2.  **Application - AuthenticationService:**
    *   Logic for tracking auth status and session timeouts (2-minute grace period).
3.  **Presentation - Auth Overlay:**
    *   A blocking overlay in `MainActivity` that requires authentication before showing any gallery content.
4.  **Optimization - ViewModel Scoping:**
    *   Scope `GalleryViewModel` and `TagViewModel` to `MainActivity` for persistent state across navigation.

## Expected Output
*   App requires fingerprint/face unlock or device password on launch.
*   Gallery data is hidden behind an overlay until authentication is successful.
*   App re-locks after being in the background for more than 2 minutes.
*   Durable state across all tabs in the app session.
