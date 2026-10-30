# 💊 MedReminder

**MedReminder** is a modern, privacy-focused Android application built with **Jetpack Compose**, **Material 3**, **Room**, and **Hilt**. It helps users track, schedule, and receive timely reminders for their daily medication intake.

---

## ✨ Features

- **Daily Dashboard**: View all scheduled medications for today with one-tap logging (Mark as Taken, Snooze, or Skip).
- **Medication Management**: Easily add, edit, or remove medications with custom dosages, times, frequencies, and instructions.
- **Weekly Overview**: Track medication adherence history over a 7-day calendar view.
- **Reliable Exact Notifications**: Uses Android `AlarmManager` exact alarms with actionable notification prompts (Confirm All, Snooze 30 mins, Reschedule for Next Day).
- **Boot Persistence**: Automatically restores scheduled alarms when the device restarts.
- **Customizable Settings**: Choose custom alarm ringtones and configure notification preferences.
- **Modern UI**: Clean, responsive layout built using Jetpack Compose and Material 3 design system.

---

## 🛠️ Tech Stack & Architecture

- **Language**: [Kotlin](https://kotlinlang.org/) (v2.0.21)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with [Material 3](https://developer.android.com/jetpack/compose/designsystems/material3)
- **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern
- **Dependency Injection**: [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- **Local Storage**: [Room Database](https://developer.android.com/training/data-storage/room) with [KSP](https://kotlinlang.org/docs/ksp-overview.html)
- **Navigation**: [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- **Asynchronous Work**: Kotlin Coroutines & `StateFlow`
- **Background Work**: `AlarmManager`, `BroadcastReceiver` for notifications and boot restoration

---

## 📂 Project Structure

```
com.example.medreminder/
├── data/
│   ├── database/       # Room DB, Entities (Medication, MedicationLog), DAOs
│   ├── preferences/    # User settings and DataStore/SharedPreferences
│   └── repository/     # Medication & Log repositories
├── di/                 # Hilt Modules (DatabaseModule, AppModule)
├── notification/       # AlarmScheduler, AlarmReceiver, BootReceiver, NotificationHelper
├── ui/
│   ├── addmedication/  # Add & Edit Medication UI & ViewModel
│   ├── home/           # Daily Home Dashboard UI & ViewModel
│   ├── medications/    # All Medications list UI & ViewModel
│   ├── navigation/     # Compose NavGraph & Screen routes
│   ├── settings/       # Settings UI, Ringtone picker & ViewModel
│   ├── theme/          # Material 3 Color, Type, and Theme
│   └── weekly/         # Weekly Adherence view UI & ViewModel
└── util/               # Time and helper utilities
```

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio**: Ladybug / Meerkat or newer
- **JDK**: Java 17 or higher
- **Android SDK**: `compileSdk = 35`, `minSdk = 31` (Android 12+)

### Building the Project

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/MedReminder.git
   cd MedReminder
   ```

2. **Open in Android Studio** and wait for Gradle synchronization to finish.

3. **Build & Run**:
   - Run from Android Studio (`Shift + F10`)
   - Or build via Gradle CLI:
     ```bash
     ./gradlew :app:assembleDebug
     ```

---

## 📄 License

```text
Copyright 2025 MedReminder

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
