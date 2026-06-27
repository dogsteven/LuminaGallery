# Phase 7: Biometric Security & App Lock

## Description
Protect the gallery with biometric authentication.

## Tasks
1.  **Infrastructure - BiometricManager:**
    *   Wrapper for `BiometricPrompt`.
2.  **Application - AuthenticationService:**
    *   Logic for tracking auth status and session timeouts.
3.  **Presentation - Auth Interceptor:**
    *   A splash or overlay screen that requires authentication before showing any gallery content.

## Expected Output
*   App requires fingerprint/face unlock on launch.
*   Gallery data is hidden until authentication is successful.
