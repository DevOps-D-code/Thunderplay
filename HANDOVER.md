# Thunderplay - Project Handover & Documentation

## 1. Project Overview
Thunderplay is a modern Android music streaming application built with **Kotlin** and **Jetpack Compose**. It features a robust playback engine using **Media3 (ExoPlayer)**, backend integration with **Firebase** (Auth, Firestore, Storage), and **JioSaavn API** for music data.

### Key Features
*   **Music Streaming**: High-quality streaming with background playback support.
*   **Local Downloads**: Download tracks for offline listening.
*   **Search**: Integrated search using JioSaavn API.
*   **Favorites & Playlists**: User-managed libraries backed by Room Database and Firebase.
*   **Modern UI**: Material 3 design with dark mode and dynamic theming.
*   **Bulk Management**: Python scripts for bulk uploading music to Firebase.

---

## 2. Technical Stack & Architecture

### Mobile App (Android)
*   **Language**: Kotlin 2.0+
*   **UI Framework**: Jetpack Compose (Material 3)
*   **Architecture Pattern**: MVVM (Model-View-ViewModel) with Clean Architecture principles.
*   **Dependency Injection**: Hilt
*   **Networking**: Retrofit2 & OkHttp3
*   **Local Database**: Room Database
*   **Media Engine**: Android Media3 (ExoPlayer)
*   **Image Loading**: Coil 3
*   **Build System**: Gradle (Kotlin DSL)

### Backend & Cloud (Firebase)
*   **Authentication**: Firebase Auth (Email/Password, Google Sign-in)
*   **Database**: Cloud Firestore (Metadata, user profiles, playlists)
*   **Storage**: Firebase Storage (Audio files, album art)

### Automation (Python)
*   **Scripts**: Python 3.x scripts for metadata fixing and bulk uploading to Firebase.
*   **Libraries**: `firebase-admin`, `requests`, `mutagen` (audio tagging).

---

## 3. Setup & Installation

### Prerequisites
*   Android Studio Ladybug (or newer recommended)
*   JDK 17
*   Python 3.10+ (for helper scripts)

### Step 1: Clone the Repository
```bash
git clone https://github.com/DevOps-D-code/Thunderplay.git
cd Thunderplay
```

### Step 2: Configure Firebase (Android App)
The app requires `google-services.json` to connect to Firebase.
1.  Go to the [Firebase Console](https://console.firebase.google.com/).
2.  Select your project (or create one).
3.  Add an Android app with package name: `com.thunderplay`.
4.  Download `google-services.json`.
5.  Place it in the `app/` directory:
    ```
    Thunderplay/
    ├── app/
    │   ├── google-services.json  <-- PLACE HERE
    │   └── src/
    ```

### Step 3: Configure Firebase Admin (Python Scripts)
To run the upload scripts (`upload_music.py`, etc.), you need the Admin SDK key.
1.  In Firebase Console, go to **Project Settings > Service accounts**.
2.  Click **Generate new private key**.
3.  Rename the downloaded file to `serviceAccountKey.json`.
4.  Place it in the root project directory:
    ```
    Thunderplay/
    ├── serviceAccountKey.json    <-- PLACE HERE
    ```
**Note**: Both `google-services.json` and `serviceAccountKey.json` are **ignored** by git for security.

### Step 4: Build & Run
Open the project in Android Studio, sync Gradle, and run on an emulator (API 35 recommended) or physical device.

---

## 4. Environment & Secrets
**Warning**: Never commit API keys or secrets to the repository.

### Required Secrets
*   **Firebase Config**: Managed via `google-services.json`.
*   **JioSaavn API**: currently integrated directly; ensure endpoints in `NetworkModule.kt` are up to date.

---

## 5. Folder Structure
```
app/src/main/java/com/thunderplay/
├── broadcast/       # BroadcastReceivers (e.g., DownloadCompleted)
├── data/            # Repositories, API interfaces, Room DAOs
├── di/              # Hilt Modules (Network, Database, App)
├── domain/          # Domain models and Business logic
├── player/          # Media3 Service and Controller logic
├── ui/              # Compose Screens, Navigation, Theme
└── utils/           # Helper functions and Constants
```

## 6. Python Automation Scripts
Located in the project root, these scripts help manage the music library.
*   `upload_music.py`: Scans `music_to_upload/` folder and uploads mp3s to Firebase Storage + Metadata to Firestore.
*   `fix_urls.py`: Utility to batch update/fix broken URLs in Firestore.

**Usage:**
```bash
pip install firebase-admin requests
python upload_music.py
```
*(Ensure `serviceAccountKey.json` is present)*

---

## 7. Known Issues & TODOs
*   **Playback**: Ensure the device has internet access; emulator proxies can sometimes block streaming.
*   **Permissions**: Android 13+ requires granular media permissions; these are handled but verify on real devices.
*   **Search**: improved debouncing could be added for smoother API calls.
