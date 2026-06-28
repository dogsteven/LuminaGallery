# Phase 7: Biometric Security & App Lock

## Description
Protect the gallery with biometric and device credential authentication using a UI-only overlay to maintain business logic isolation.

## Technical Scope / Expected Deliverables
1.  **Infrastructure - BiometricManager:**
    *   `BiometricPrompt` implementation supporting `BIOMETRIC_STRONG` and `DEVICE_CREDENTIAL`.
2.  **Application - AuthenticationService:**
    *   Auth status tracking and 2-minute grace period timeout logic.
3.  **Presentation - Auth Overlay:**
    *   Blocking `AuthScreen` overlay in `MainActivity` gating access to the gallery.
4.  **Architecture - ViewModel Scoping:**
    *   Activity-scoping for core ViewModels to ensure state persistence across navigation.

## Expected Output
*   App requires fingerprint/face unlock or device password on launch.
*   Gallery data is hidden behind an overlay until authentication is successful.
*   App re-locks after being in the background for more than 2 minutes.
*   Durable state across all tabs in the app session.
