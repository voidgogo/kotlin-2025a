# Mobile Programming, Fall 2025 Semester

This repository contains the source code and documentation for Week 05 of the Kotlin learning project, developed in Android Studio using XML Views. The focus is on ListView and data handling with simple interactive Android apps.

## Project Overview
- **Path**: `D:\kotlin-2025a\AppWeek05`
- **Environment**: Android Studio, Kotlin, XML Views (Empty Views Activity)
- **Purpose**: Practice ListView, ArrayAdapter, and data manipulation with simple apps: Student List, Shopping Cart, and Task Manager.
- **Structure**: Single Activity (`MainActivity.kt`) with ListView integration and data management.

## Week 05: ListView and Data Handling

### Objectives
- Understand ListView implementation and ArrayAdapter usage in Android.
- Apply data management techniques with ArrayList and user interaction.
- Build interactive apps with dynamic list manipulation: Add, Remove, Update operations.

### Content
- **Project**: `AppWeek05` (XML Views with ListView).
- **Main Branch**: Student List App (`MainActivity.kt`, `activity_main.xml`).
    - Add student names via EditText, display in ListView.
    - Remove items by long-clicking on ListView items.
    - Logcat tag: `"KotlinWeek05App"`.
- **Branches**:
    - `week05/shopping-cart`: Add shopping cart app (Add/Remove items, display total count).
    - `week05/task-manager`: Add simple task manager (Add tasks, mark as completed, remove).
- **Workflow**:
    1. Start from `main` (Student List App).
    2. Create feature branches: `git checkout -b week05/shopping-cart`.
    3. Develop and commit changes to the project folder (e.g., `git add AppWeek05/`).
    4. Merge to `main`: `git merge week05/shopping-cart` (resolve conflicts if needed).
- **AndroidManifest.xml**: Updated to launch `MainActivity` as the main entry point.

### Key Components
- **ListView**: Display dynamic list of items
- **ArrayAdapter**: Manage data binding between ArrayList and ListView
- **EditText**: Input field for adding new items
- **Button**: Add/Clear/Reset operations
- **ArrayList<String>**: Data storage for list items
- **OnItemLongClickListener**: Handle item removal

### How to Run
1. Open the project in Android Studio (`D:\kotlin-2025a\AppWeek05`).
2. Build and run on an emulator or device (API 24+ recommended).
3. Open Logcat and filter by tag `"KotlinWeek05App"` to view outputs (e.g., item additions, removals, list operations).

### Exercises
- Add Toast messages for successful operations (add/remove items).
- Implement item editing functionality (click to edit existing items).
- Add data validation (prevent empty items from being added).
- Extend the task manager: Add priority levels (High, Medium, Low) and sorting.
- Implement data persistence using SharedPreferences.

## Sample Code Structure

### MainActivity.kt (Main Branch - Student List)
```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var studentList: ArrayList<String>
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var listView: ListView
    private lateinit var editTextStudent: EditText
    private lateinit var buttonAdd: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize components
        setupViews()
        setupListView()
        setupListeners()
    }
}
```

### activity_main.xml Layout
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">
    
    <EditText
        android:id="@+id/editTextStudent"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Enter student name" />
    
    <Button
        android:id="@+id/buttonAdd"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Add Student" />
    
    <ListView
        android:id="@+id/listViewStudents"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1" />
        
</LinearLayout>
```

## Setup Instructions
1. **Create New Project** in Android Studio:
    - Choose "Empty Views Activity"
    - Project name: `AppWeek05`
    - Package name: `com.appweek05`
    - Save location: `D:\kotlin-2025a\AppWeek05`

2. **Initial Setup**:
   ```bash
   cd D:\kotlin-2025a
   git add AppWeek05/
   git commit -m "week 05: ListView Apps initialization"
   ```

3. Ensure Android SDK and emulator are configured.
4. Sync project with Gradle files.

## Git Workflow
- Continue using the existing Git repository in `D:\kotlin-2025a`.
- To commit Week 05 changes:
  ```bash
  git add AppWeek05/
  git commit -m "week 05: Student List App v0.1"
  git push origin main
  ```
- Create feature branches:
  ```bash
  git checkout -b week05/shopping-cart
  git checkout -b week05/task-manager
  ```

## Learning Outcomes
By completing Week 05, students will:
- Master ListView implementation and data binding
- Understand ArrayAdapter usage for dynamic lists
- Practice user input validation and data manipulation
- Learn item selection and removal techniques
- Apply Git branching strategies for feature development