# Mobile Programming, Fall 2025 Semester

This repository contains the source code and documentation for Week 04 of the Kotlin learning project, developed in Android Studio using XML Views. The focus is on Git workflows with interactive Android apps.

## Project Overview
- **Path**: `D:\kotlin-2025a\AppWeek04`
- **Environment**: Android Studio, Kotlin, XML Views (Empty Views Activity)
- **Purpose**: Practice Git branching/merging while building simple apps: Greeting, Color Changer, and Counter.
- **Structure**: Single Activity (`GreetingActivity.kt`) with progressive features added via branches.

## Week 04: Git Workflow with Android Apps
### Objectives
- Understand Git branching, merging, and conflict resolution in a monorepo setup.
- Apply UI components (EditText, Button, TextView) in XML Views.
- Build simple interactive apps: Greeting, Color Changer, and Counter.

### Content
- **Project**: `AppWeek04` (XML Views).
- **Main Branch**: Greeting App only (`GreetingActivity.kt`, `activity_greeting.xml`).
  - Input name via EditText, display greeting on button click.
  - Logcat tag: `"KotlinWeek04App"`.
- **Branches**:
  - `week04/color-changer`: Add color changer app (Red/Green/Blue buttons for background color change).
  - `week04/counter`: Add counter app (+, -, Reset buttons for increment/decrement/reset).
- **Workflow**:
  1. Start from `main` (Greeting App).
  2. Create feature branches: `git checkout -b week04/color-changer`.
  3. Develop and commit changes to the project folder (e.g., `git add AppWeek04/`).
  4. Merge to `main`: `git merge week04/color-changer` (resolve conflicts if needed).
- **AndroidManifest.xml**: Updated to launch `GreetingActivity` as the main entry point.

### How to Run
1. Open the project in Android Studio (`D:\kotlin-2025a\AppWeek04`).
2. Build and run on an emulator or device (API 24+ recommended).
3. Open Logcat and filter by tag `"KotlinWeek04App"` to view outputs (e.g., greeting messages, color changes, counter updates).

### Exercises
- Add Toast messages for button clicks in each app.
- Resolve merge conflicts by editing overlapping code in `GreetingActivity.kt` or `activity_greeting.xml`.
- Simulate Pull Requests: Push branches to GitHub and review/merge via web interface.
- Extend the counter: Use a `List<Int>` to store counter history and log statistics.

## Setup Instructions
1. **Clone the Repository** (if not already local):
   ```bash
   git clone <repository-url>
   ```
2. Open `D:\kotlin-2025b\AppWeek04` in Android Studio.
3. Ensure the Android SDK and a compatible emulator/device are configured.
4. Sync the project with Gradle (`File > Sync Project with Gradle Files`).

## Git Workflow
- The project is version-controlled with Git (`.git` folder in `D:\kotlin-2025a` root).
- To update with Week 04 changes:
  ```bash
  git add AppWeek04/
  git commit -m "week 04: Greeting Apps v0.1"
  git push origin main
  ```
- Create a branch for new features:
  ```bash
  git checkout -b week04/new-feature
  ```