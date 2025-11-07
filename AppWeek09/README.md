# Mobile Programming, Fall 2025 Semester

This repository contains the source code and documentation for Week 09 of the Kotlin learning project, developed in Android Studio using XML Views. The focus is on RecyclerView fundamentals, ViewHolder pattern, and migration from ListView to modern Android components.

## Project Overview
- **Path**: `D:\kotlin-2025a\AppWeek09`
- **Environment**: Android Studio, Kotlin, XML Views (Empty Views Activity)
- **Purpose**: Master RecyclerView architecture, ViewHolder pattern optimization, and various LayoutManager implementations for efficient list rendering.
- **Structure**: Single Activity (`MainActivity.kt`) with RecyclerView, multiple LayoutManager support (Linear, Grid), and comprehensive data management.

## Week 09: RecyclerView Fundamentals

### Objectives
- Understand RecyclerView architecture and lifecycle management.
- Master ViewHolder pattern for performance optimization.
- Implement multiple LayoutManager types for flexible layouts.
- Apply event handling (click, long-click) with proper callbacks.
- Optimize adapter performance using notifyItem methods.

### Content
- **Project**: `AppWeek09` (XML Views with RecyclerView).
- **Main Branch**: RecyclerView Student Manager with dynamic layout switching.
- **Key Components**:
    - `Student.kt`: Data model with UUID generation
    - `StudentAdapter.kt`: RecyclerView adapter with ViewHolder pattern
    - `MainActivity.kt`: Main activity with RecyclerView initialization
    - XML Layouts: activity_main.xml and item_student.xml

### RecyclerView Architecture

#### Core Components
```
RecyclerView (Container)
├── Adapter (Data ↔ View Binding)
│   ├── onCreateViewHolder()  [Infrequent calls]
│   ├── onBindViewHolder()    [Frequent calls]
│   └── getItemCount()         [Returns total items]
├── ViewHolder (View Caching)
│   ├── itemView reference
│   └── bind() method
├── LayoutManager (Layout Rules)
│   ├── LinearLayoutManager
│   ├── GridLayoutManager
│   └── StaggeredGridLayoutManager
└── ItemDecoration (Dividers, spacing)
```

### Key Features

#### 1. Student List Display
- Display student information in RecyclerView
- 6 sample students with initial data
- Show name, department, grade, and email
- Efficient view recycling for performance

#### 2. Student Management
- **Add Students**: EditText input with validation
- **Remove Students**: Long-click for deletion
- **Clear All**: Remove all students at once
- **Duplicate Detection**: Prevent duplicate entries
- **Statistics**: Display total student count

#### 3. Dynamic Layout Switching
- **Linear Layout**: Vertical scrollable list
- **Grid Layout (2 columns)**: 2-column grid display
- **Grid Layout (3 columns)**: 3-column grid display
- **Runtime Switching**: Change layouts without data loss
- **Spinner Control**: Easy layout selection

#### 4. Item Interaction
- **Click Event**: Display student details in dialog
- **Long-click Event**: Show deletion confirmation
- **Dialog Information**: Full student details display
- **Visual Feedback**: Toast messages for user actions

### How to Run
1. Open the project in Android Studio (`D:\kotlin-2025a\AppWeek09`).
2. Ensure `build.gradle` has ViewBinding enabled and RecyclerView dependency added.
3. Perform Gradle sync (File > Sync Now).
4. Build and run on an emulator or device (API 24+ recommended).
5. Use the layout spinner to switch between Linear and Grid layouts.
6. Open Logcat and filter by tag `"KotlinWeek09App"` to view detailed operations.

### Sample Data Structures

#### Student Data Model
```kotlin
data class Student(
    val name: String,
    val id: String = UUID.randomUUID().toString(),
    val department: String = "Computer Science",
    val grade: String = "1st Year",
    val email: String = "",
    val addedDate: Date = Date()
) {
    override fun toString(): String = name
}
```

#### StudentAdapter with ViewHolder
```kotlin
class StudentAdapter(
    private val studentList: List<Student>,
    private val onItemClick: (Student, Int) -> Unit,
    private val onItemLongClick: (Int) -> Unit
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {
    
    inner class StudentViewHolder(private val binding: ItemStudentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(student: Student) {
            binding.apply {
                textViewName.text = student.name
                textViewDept.text = student.department
                textViewGrade.text = "Grade: ${student.grade}"
                textViewEmail.text = student.email
                
                root.setOnClickListener {
                    onItemClick(student, adapterPosition)
                }
                root.setOnLongClickListener {
                    onItemLongClick(adapterPosition)
                    true
                }
            }
        }
    }
    
    override fun onCreateViewHolder(...): StudentViewHolder { ... }
    override fun onBindViewHolder(...) { ... }
    override fun getItemCount(): Int = studentList.size
}
```

### Performance Optimization

#### ViewHolder Pattern Benefits
- **View Caching**: Store view references to avoid repeated findViewById() calls
- **Memory Efficiency**: Reuse views when scrolling instead of creating new ones
- **Smooth Scrolling**: Reduced computational overhead during scroll events
- **Battery Life**: Lower CPU usage extends device battery duration

#### Best Practices
```kotlin
// Use notifyItem methods for specific updates
adapter.notifyItemInserted(position)
adapter.notifyItemRemoved(position)
adapter.notifyItemChanged(position)

// Full adapter refresh (expensive)
adapter.notifyDataSetChanged()
```

### LayoutManager Types

#### LinearLayoutManager
```kotlin
recyclerView.layoutManager = LinearLayoutManager(
    this,
    LinearLayoutManager.VERTICAL,  // Direction
    false                           // Reverse
)
```

#### GridLayoutManager
```kotlin
recyclerView.layoutManager = GridLayoutManager(
    this,
    2  // Column count
)
```

#### Switching at Runtime
```kotlin
binding.spinnerLayout.onItemSelectedListener = 
    object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, 
                                   view: View?, 
                                   position: Int, 
                                   id: Long) {
            recyclerView.layoutManager = when(position) {
                0 -> LinearLayoutManager(this@MainActivity)
                1 -> GridLayoutManager(this@MainActivity, 2)
                2 -> GridLayoutManager(this@MainActivity, 3)
                else -> LinearLayoutManager(this@MainActivity)
            }
        }
        override fun onNothingSelected(parent: AdapterView<*>) {}
    }
```

### Build Configuration

#### Required Dependencies
```gradle
dependencies {
    implementation 'androidx.recyclerview:recyclerview:1.4.0'
}
```

#### ViewBinding Setup
```gradle
android {
    buildFeatures {
        viewBinding = true
    }
}
```

### Project Structure
```
AppWeek09/
├── app/src/main/java/com/example/appweek09/
│   ├── MainActivity.kt              # Main Activity with RecyclerView
│   ├── StudentAdapter.kt            # RecyclerView Adapter
│   └── data/
│       └── Student.kt               # Data Model
├── app/src/main/res/layout/
│   ├── activity_main.xml            # Main Activity Layout
│   └── item_student.xml             # RecyclerView Item Layout
└── app/build.gradle                 # Build Configuration
```

### Learning Outcomes
By completing Week 09, students will:
- Master RecyclerView architecture and component relationships
- Understand ViewHolder pattern for performance optimization
- Implement multiple LayoutManager types dynamically
- Apply efficient adapter methods (notifyItem vs notifyDataSetChanged)
- Handle item events (click, long-click) with callbacks
- Practice Android best practices for list rendering

### Comparison: ListView vs RecyclerView

| Aspect | ListView | RecyclerView |
|--------|----------|------------|
| **Performance** | Incomplete view reuse | Complete view recycling |
| **Memory Usage** | Higher | Lower |
| **Layout Types** | Single type only | Multiple (Linear, Grid, Staggered) |
| **ViewHolder Pattern** | Optional | Built-in |
| **Animations** | Limited API | Full support |
| **Item Click Handler** | Built-in | Callback-based |
| **Learning Curve** | Easier | Steeper |
| **Production Use** | Legacy | Current standard |

### Resources

#### Official Documentation
- [Android RecyclerView Guide](https://developer.android.com/guide/topics/ui/layout/recyclerview)
- [ViewHolder Pattern](https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.ViewHolder)
- [LayoutManager Documentation](https://developer.android.com/reference/androidx/recyclerview/widget/LayoutManager)

#### Related Topics
- Week 05: ListView and ArrayAdapter (Foundation)

### Final Notes

RecyclerView is the **modern standard** for displaying lists in Android. Mastering it is essential for professional Android development. Focus on understanding the ViewHolder pattern and how view recycling improves performance—these concepts extend to other Android frameworks and best practices.