# Mobile Programming, Fall 2025 Semester

This repository contains the source code and documentation for a Kotlin learning project, developed in Android Studio using the Compose Empty Activity template. The project is structured as a series of weekly exercises to build foundational Kotlin skills for Android development.

## Project Overview
- **Path**: `D:\kotlin-2025a\KotlinBasics`
- **Environment**: Android Studio, Kotlin, Jetpack Compose (Empty Activity)
- **Purpose**: Learn Kotlin fundamentals through hands-on coding, with outputs verified via Logcat.
- **Structure**: Weekly functions (`weekXX...()`) called in `MainActivity.kt` to demonstrate concepts.

## Week 01-02: Variables and Functions
### Objectives
- Understand basic variables (`val`/`var`) and data types.
- Define and call functions with parameters and return types.

### Content
- Implemented `week02Variables()` to practice variable declarations and scope.
- Implemented `week02Functions()` to explore function definitions, default parameters, and lambda expressions.
- Outputs logged to Logcat with tag `"KotlinWeek02"`.

## Week 03: Classes and Collections
### Objectives
- Master object-oriented programming with Kotlin classes (properties, constructors, inheritance, data classes).
- Explore Kotlin collections (`List`, `Set`, `Map`) and operations (`filter`, `map`, `forEach`).

### Content
1. **`week03Classes()`**:
   - Basic class creation (`Person`) with properties and methods.
   - Primary and secondary constructors, inheritance (`Animal` → `Dog`).
   - Data classes (`Book`) for automatic `equals`, `hashCode`, and `toString`.
   - Example: Create a `Person` instance and log its introduction.
2. **`week03Collections()`**:
   - Immutable/mutable collections: `List`, `Set`, `Map`.
   - Operations using lambdas: `filter` for evens, `map` for squares.
   - Custom extension function (`printEach`) for `List`.
   - Example: Filter a list of fruits and log unique counts.

### How to Run
1. Open the project in Android Studio (`D:\kotlin-2025a\KotlinBasics`).
2. Build and run on an emulator or device (API 34+ recommended).
3. Open Logcat and filter by tag `"KotlinWeek03"` to view outputs.

### Exercises
- Extend `Person` class: Add a method to check if the person is an adult (`age >= 20`).
- Create a `Dog` class inheriting from `Animal` with a custom `makeSound()`.
- Use collections to compute statistics (e.g., average score from a `List` of student scores).

## Setup Instructions
1. **Clone the Repository** (if not already local):
   ```bash
   git clone <repository-url>
