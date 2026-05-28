Multi-Tenant Gym Management System (Android App)
A modern Android application built using Kotlin and Jetpack Compose that simulates a scalable multi-tenant Gym Management System. This project demonstrates a clean architecture approach with fully navigable UI screens, role-based dashboards, and modular design—without backend integration (uses mock data).

🚀 Key Features
🏢 Multi-Tenant Concept (UI Simulation)
Supports multiple gym owners with isolated workflows (simulated using role-based navigation)

👥 Role-Based Access
Super Admin
Gym Owner
Trainer
Member

📱 Modern UI/UX
Jetpack Compose + Material 3
Clean, responsive design
Dark mode support

🔄 Full Navigation Flow
Splash → Login → Role-Based Dashboard
Bottom Navigation + Screen Routing

📊 Core Modules (UI Simulation)
Dashboard (analytics cards)
Members Management
Trainers Management
Subscription Plans
Payments & History
Attendance Tracking
Reports & Settings

🧠 Architecture
MVVM (Model-View-ViewModel)
Modular structure
Reusable components

🎯 Mock Data Support
Uses dummy data to simulate real-world gym operations (no backend/database yet)
# Run and deploy your AI Studio app

This contains everything you need to run your app locally.
View your app in AI Studio: https://ai.studio/apps/396d2316-82fd-49f3-9850-88b4b824657f

## Run Locally

**Prerequisites:**  [Android Studio](https://developer.android.com/studio)


1. Open Android Studio
2. Select **Open** and choose the directory containing this project
3. Allow Android Studio to fix any incompatibilities as it imports the project.
4. Create a file named `.env` in the project directory and set `GEMINI_API_KEY` in that file to your Gemini API key (see `.env.example` for an example)
5. Remove this line from the app's `build.gradle.kts` file: `signingConfig = signingConfigs.getByName("debugConfig")`
6. Run the app on an emulator or physical device
