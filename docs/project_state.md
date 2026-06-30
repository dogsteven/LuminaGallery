# Project State & Functionality Map

This document provides a high-level overview of LuminaGallery's current state and core functionalities to help developers and AI agents catch up quickly.

## 1. Project Overview
LuminaGallery is a secure, local-first private image gallery.
- **Current Status:** Post-Refinement (Completed "First Polishing" Epic).
- **Core Philosophy:** Data isolation, biometric protection, and fluid UX.

## 2. Core Functionalities

### 2.1 Security & Privacy
- **Biometric Protection:** Mandatory authentication on app launch with a 2-minute background grace period.
- **Content Protection:** `FLAG_SECURE` is active to hide content in the system task switcher and block screenshots.
- **Isolated Storage:** Images are stored in the app's internal `filesDir/vault`, inaccessible to other apps.
- **Related Docs:**
    - [Image Gallery Summary (Phase 7)](implementation/image_gallery/summary.md)
    - [First Polishing Summary (Phase 2)](implementation/first_polishing/summary.md)

### 2.2 Image Viewing & Interaction
- **High-Performance Grid:** Capable of handling 10,000+ items using pre-generated 256x256 thumbnails.
- **Advanced Viewer:** Full-screen viewing with pinch-to-zoom and panning via **ZoomImage**.
- **Transformation:** 90-degree internal rotation that maintains gesture alignment.
- **Gestures:** Fluid "Swipe-to-Dismiss" active only at 1x zoom levels.
- **Related Docs:**
    - [First Polishing Summary (Phase 4)](implementation/first_polishing/summary.md)
    - [Technical Guide (Image Loading)](technical_guide.md)

### 2.3 Organization & Metadata
- **Tagging System:** Flexible, stable string-based tagging for image categorization.
- **Search & Filtering:** Multi-criteria filtering (Tags, Date, Text) with support for **Saved Criteria**.
- **Unified Management:** `DetailContentSheet` provides a central hub for editing descriptions, managing tags, and deleting images.
- **Related Docs:**
    - [First Polishing Summary (Phase 1 & 3)](implementation/first_polishing/summary.md)
    - [Architecture Guide (Data Model Layer)](architecture_guide.md)

### 2.4 Navigation & State
- **Structure:** Modern `ModalNavigationDrawer` for scalable feature access.
- **Persistence:** Activity-scoped ViewModels ensure navigation and filter states persist throughout the session.
- **Related Docs:**
    - [First Polishing Summary (Phase 2)](implementation/first_polishing/summary.md)

## 3. Technical Foundation
- **Architecture:** Strict **Layered Architecture** (Data Model -> Application -> Infrastructure -> Presentation).
- **Primary Libraries:** Jetpack Compose (M3), Hilt (DI), Room (Persistence), ZoomImage (ImageViewer), Coil 3 (Loading).
- **Data Model:** Stable identifiers and composite primary keys `(source, identifier)` for images.
- **Related Docs:**
    - [Architecture Guide](architecture_guide.md)
    - [Technical Guide](technical_guide.md)
    - [AGENTS.md](../AGENTS.md) (Strict rules and tech stack)

## 4. Historical Context
- **Epic 1: Private Image Gallery Core:** Established the foundation of secure storage and basic gallery features. [Read Summary](implementation/image_gallery/summary.md).
- **Epic 2: First Polishing:** Refined the data model, migrated navigation, and perfected gestures/interactions. [Read Summary](implementation/first_polishing/summary.md).
