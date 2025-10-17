# Mobile Programming, Fall 2025 Semester

This repository contains the source code and documentation for Week 06 of the Kotlin learning project, developed in Android Studio using XML Views. The focus is on Git branching strategies and feature integration with expandable ListView applications.

## Project Overview
- **Path**: `D:\kotlin-2025a\AppWeek06`
- **Environment**: Android Studio, Kotlin, XML Views (Empty Views Activity)
- **Purpose**: Practice Git feature branching, merging, and conflict resolution while building expandable apps with multiple functionalities.
- **Structure**: Single Activity (`MainActivity.kt`) with mode switching between Student List, Shopping Cart, and Task Manager.

## Week 06: Git Branching and Feature Integration

### Objectives
- Master Git feature branching workflow and merge conflict resolution.
- Implement multiple app functionalities within a single codebase.
- Apply advanced ListView techniques with different data types and adapters.
- Build modular, extensible application architecture.

### Content
- **Project**: `AppWeek06` (XML Views with Multiple Features).
- **Main Branch**: Student List App (base functionality from Week 05).
- **Feature Branches**:
    - `week06/shopping-cart`: Shopping cart with items, quantities, and total price calculation.
    - `week06/task-manager`: Task manager with priority levels, completion status, and due dates.
    - `week06/integration`: Merged branch with all three features and mode switching.

### Git Workflow Strategy
```bash
# 1. Start from main (Student List)
git checkout main
git pull origin main

# 2. Create and work on shopping cart feature
git checkout -b week06/shopping-cart
# Develop shopping cart functionality
git add AppWeek06/
git commit -m "week06 : Shopping Cart App v0.1"

# 3. Create and work on task manager feature
git checkout main
git checkout -b week06/task-manager
# Develop task manager functionality
git add AppWeek06/
git commit -m "week06 : Task Manager App v0.1"

# 4. Integration branch
git checkout main
git checkout -b week06/integration
git merge week06/shopping-cart
git merge week06/task-manager
# Resolve conflicts and test integration
git add AppWeek06/
git commit -m "week 06: Integrate all features with mode switching"

# 5. Merge back to main
git checkout main
git merge week06/integration
```

### Key Features

#### 1. Student List Manager (Base - Main Branch)
- Add/remove student names
- Display total count
- Long press to delete
- Initial sample data

#### 2. Shopping Cart (Branch: week06/shopping-cart)
- Add items with quantities
- Price calculation per item
- Total cart value display
- Remove items
- Clear entire cart
- Item details with quantity adjustment

#### 3. Task Manager (Branch: week06/task-manager)
- Add tasks with priority levels (High, Medium, Low)
- Mark tasks as completed/incomplete
- Due date setting
- Filter by priority
- Sort by completion status
- Task statistics display

#### 4. Mode Switching (Integration Branch)
- Radio buttons or Spinner for mode selection
- Dynamic UI updates based on selected mode
- Shared ListView with different adapters
- Persistent mode selection

### Enhanced Components
- **Multiple Data Models**: Student, CartItem, Task classes
- **Custom Adapters**: Different adapters for each mode
- **Mode Management**: Enum-based mode switching
- **Data Persistence**: SharedPreferences for mode and data
- **Advanced UI**: Mode-specific layouts and controls
- **Validation**: Enhanced input validation for each mode

### How to Run
1. Open the project in Android Studio (`D:\kotlin-2025a\AppWeek06`).
2. Build and run on an emulator or device (API 24+ recommended).
3. Use mode selector to switch between Student List, Shopping Cart, and Task Manager.
4. Open Logcat and filter by tag `"KotlinWeek06App"` to view detailed operations.

### Sample Data Structures

#### Student Data Model
```kotlin
data class Student(
    val name: String,
    val id: String = UUID.randomUUID().toString(),
    val addedDate: Date = Date()
)
```

#### Shopping Cart Item Model
```kotlin
data class CartItem(
    val name: String,
    var quantity: Int = 1,
    val price: Double,
    val id: String = UUID.randomUUID().toString(),
    val addedDate: Date = Date()
) {
    fun getTotalPrice(): Double = quantity * price
}
```

#### Task Model
```kotlin
data class Task(
    val title: String,
    val description: String = "",
    var isCompleted: Boolean = false,
    val priority: TaskPriority,
    val dueDate: Date? = null,
    val id: String = UUID.randomUUID().toString(),
    val createdDate: Date = Date()
)

enum class TaskPriority(val displayName: String, val color: Int) {
    HIGH("High", Color.RED),
    MEDIUM("Medium", Color.BLUE),
    LOW("Low", Color.GREEN)
}
```

### App Modes Enum
```kotlin
enum class AppMode(val displayName: String) {
    STUDENT_LIST("Student List"),
    SHOPPING_CART("Shopping Cart"),
    TASK_MANAGER("Task Manager")
}
```

### Learning Outcomes
By completing Week 06, students will:
- Master Git feature branching and merge conflict resolution
- Understand modular application architecture
- Implement multiple data models and custom adapters
- Practice advanced ListView techniques and mode switching
- Apply software engineering principles for maintainable code

### Conflict Resolution Examples
Common merge conflicts and solutions:
1. **Layout conflicts**: Different branches modifying same XML elements
2. **Method conflicts**: Same method name with different implementations
3. **Import conflicts**: Different import statements for same functionality
4. **Resource conflicts**: String resources, colors, dimensions