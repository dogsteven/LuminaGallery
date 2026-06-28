# Phase 2: Scalable Navigation & Security

## Description
This phase implements the structural UI changes required for future scalability and enhances the application's privacy by securing its appearance in the system.

## Technical Scope / Expected Deliverables
1.  **Navigation Migration (Presentation):**
    - Implement `ModalNavigationDrawer` (Material 3).
    - Migrate bottom navigation items to the side drawer.
    - Implement a consistent `TopAppBar` across main screens that integrates with the drawer.
2.  **App Switcher Privacy (Infrastructure/Platform):**
    - Implement `WindowManager.LayoutParams.FLAG_SECURE` in `MainActivity` to protect content in the recents screen and prevent screenshots.

## Expected Output
*   Application navigates via a side drawer instead of a bottom bar.
*   Persistent TopAppBar providing context and navigation triggers.
*   App content is hidden in the system task switcher.
