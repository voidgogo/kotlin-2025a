# Mobile Programming, Fall 2025 Semester

# Week 11: Advanced Room Database and Flow Learning

This document covers Week 11 of the Kotlin learning project. Building on the Room Database fundamentals from Week 10, this week focuses on real-time data updates through Flow and more efficient asynchronous task processing.

## Project Overview
- **Path**: `D:\kotlin-2025a\AppWeek11`
- **Environment**: Android Studio, Kotlin, XML Views (Empty Views Activity)
- **Purpose**: Advanced Room Database utilization and reactive UI implementation with Flow
- **Structure**: Single Activity + Room Database + RecyclerView + Flow
- **Key Concepts**: Flow, lifecycleScope, suspend functions, asynchronous data processing

## Week 11 Objectives

- Understand Room Database fundamentals (Entity, DAO, Database)
- Implement automatic UI updates using Flow
- Handle safe asynchronous processing with lifecycleScope and coroutines
- Integrate RecyclerView with database operations
- Implement data validation and duplicate checking
- Apply basic error handling

## Project Content

- **Project**: `AppWeek11` (Room Database + RecyclerView + Flow)
- **Goal**: Student management app (add, view, delete, real-time updates)
- **Core Components**:
    - `Student.kt` (Entity): Database table definition
    - `StudentDao.kt` (DAO): Data access object
    - `StudentDatabase.kt`: Singleton pattern database
    - `MainActivity.kt`: UI and business logic
    - `StudentAdapter.kt`: RecyclerView adapter
    - XML Layouts: activity_main.xml, item_student.xml

## Room Database Architecture

### Three Core Components

```
Room Database Structure
├── Entity (Table Definition)
│   └── Student.kt (@Entity annotation)
│       - @PrimaryKey: Unique ID
│       - @ColumnInfo: Column naming
│
├── DAO (Data Access Object)
│   └── StudentDao.kt (@Dao annotation)
│       - @Query: SELECT queries
│       - @Insert: Data insertion
│       - @Delete: Data deletion
│       - Flow<>: Automatic updates
│
└── Database (Database Holder)
    └── StudentDatabase.kt (@Database annotation)
        - Singleton pattern
        - DAO provider methods
```

## Key Concepts Explained

### 1. What is Flow?

**Flow** automatically delivers values whenever data changes.

```kotlin
// Analogy: Like listening to a radio channel continuously
@Query("SELECT * FROM students ORDER BY id DESC")
fun getAllStudents(): Flow<List<Student>>
// - When database changes, automatically sends new list
// - UI updates automatically
```

### 2. Suspend Functions

**Suspend** means "this might take time, run in background".

```kotlin
// suspend function = does not block main thread
suspend fun insertStudent(student: Student)
suspend fun deleteStudent(student: Student)
suspend fun deleteAllStudents()
```

### 3. Singleton Pattern

Database must exist **only once** across the entire app.

```kotlin
// Bad: Multiple instances cause data corruption
val db1 = StudentDatabase.build()
val db2 = StudentDatabase.build()  // Different database!

// Good: Singleton ensures same instance always
val db = StudentDatabase.getDatabase(context)
```

## Code Implementation

### 1. Student Entity (Table Definition)

```kotlin
package com.appweek11

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Student Entity
 *
 * @Entity: Declares this class is a database table
 * tableName: Table name is "students"
 */
@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,  // Auto-incrementing ID
    
    @ColumnInfo(name = "student_name")
    val name: String,  // Student name
    
    val department: String = "Computer Science",  // Department
    val grade: String = "1st Year"  // Year
)
```

**Annotation Explanation**:
- `@Entity`: Marks this class as a database table
- `@PrimaryKey(autoGenerate = true)`: Auto-incrementing unique ID
- `@ColumnInfo`: Specifies database column name (optional)

### 2. StudentDao (Data Access Object)

```kotlin
package com.appweek11

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * StudentDao (Data Access Object)
 *
 * @Dao: Data access interface
 * Define all SELECT, INSERT, DELETE operations here
 */
@Dao
interface StudentDao {
    
    /**
     * Query all students (auto-updates with Flow)
     *
     * Flow:
     * - Automatically notifies when data changes
     * - Like listening to a radio channel continuously
     * - UI updates automatically
     */
    @Query("SELECT * FROM students ORDER BY id DESC")
    fun getAllStudents(): Flow<List<Student>>
    
    /**
     * Add student
     *
     * suspend:
     * - This function might take time
     * - Runs in background
     * - Does not freeze UI
     *
     * @Insert
     * - Adds this object to database
     * - onConflict: Replace if same ID exists
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student)
    
    /**
     * Delete specific student
     *
     * @Delete: Removes this object from database
     */
    @Delete
    suspend fun deleteStudent(student: Student)
    
    /**
     * Delete all students
     *
     * @Query: Custom SQL query
     * DELETE FROM students: "Delete entire students table"
     */
    @Query("DELETE FROM students")
    suspend fun deleteAllStudents()
}
```

**Key Points**:
- `Flow<>`: Provides automatic update functionality
- `suspend`: Doesn't block main thread
- `@Query`: Write custom SQL queries

### 3. StudentDatabase (Singleton Database)

```kotlin
package com.appweek11

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * StudentDatabase
 *
 * @Database: Database definition
 * entities: Tables included in this database
 * version: Database version (needed for future changes)
 */
@Database(entities = [Student::class], version = 1, exportSchema = false)
abstract class StudentDatabase : RoomDatabase() {
    
    // Method to provide DAO for this database
    abstract fun studentDao(): StudentDao
    
    companion object {
        /**
         * Singleton Pattern
         *
         * "Database must exist only once across the entire app"
         * - Multiple instances cause data corruption
         * - Always use getDatabase() for same instance
         */
        @Volatile  // Thread-safe reading in multithreading
        private var INSTANCE: StudentDatabase? = null
        
        fun getDatabase(context: Context): StudentDatabase {
            // Use existing if available
            if (INSTANCE != null) {
                return INSTANCE!!
            }
            
            // Create new if not exists (runs only once)
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StudentDatabase::class.java,
                    "student_database"  // Database file name
                ).build()
                
                INSTANCE = instance
                return instance
            }
        }
    }
}
```

**Singleton Pattern Explanation**:
- `@Volatile`: Thread-safe in multithreading environment
- `synchronized(this)`: Only one thread executes at a time
- `INSTANCE ?:`: Return if exists, create if not
- `context.applicationContext`: App-level context for lifecycle

### 4. StudentAdapter (RecyclerView Adapter)

```kotlin
package com.appweek11

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appweek11.databinding.ItemStudentBinding

class StudentAdapter(
    private var studentList: List<Student>,
    private val onItemClick: (Student, Int) -> Unit,
    private val onItemLongClick: (Int) -> Unit
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {
    
    inner class StudentViewHolder(private val binding: ItemStudentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(student: Student) {
            binding.apply {
                textViewName.text = student.name
                textViewDept.text = "${student.department} • ${student.grade}"
                
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
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StudentViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(studentList[position])
    }
    
    override fun getItemCount(): Int = studentList.size
    
    // Called when receiving new data from Flow
    fun updateList(newList: List<Student>) {
        studentList = newList
        notifyDataSetChanged()
    }
}
```

### 5. MainActivity (Core Logic)

```kotlin
package com.appweek11

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.appweek11.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: StudentDatabase
    private lateinit var adapter: StudentAdapter
    private var currentStudents: List<Student> = emptyList()
    
    companion object {
        const val TAG = "AppWeek11"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(TAG, "App started")
        
        // Initialize Room Database
        database = StudentDatabase.getDatabase(this)
        
        // Setup RecyclerView
        setupRecyclerView()
        
        // Setup button listeners
        setupListeners()
        
        // Start observing data (Important!)
        observeStudents()
    }
    
    /**
     * RecyclerView basic setup
     */
    private fun setupRecyclerView() {
        adapter = StudentAdapter(
            emptyList(),
            onItemClick = { student, _ ->
                // Item click -> show details
                AlertDialog.Builder(this)
                    .setTitle(student.name)
                    .setMessage("${student.department} ${student.grade}")
                    .setPositiveButton("OK", null)
                    .show()
                Log.d(TAG, "Clicked: ${student.name}")
            },
            onItemLongClick = { position ->
                // Long click -> confirm delete
                if (position < currentStudents.size) {
                    val student = currentStudents[position]
                    AlertDialog.Builder(this)
                        .setTitle("Delete?")
                        .setMessage(student.name)
                        .setPositiveButton("Delete") { _, _ ->
                            deleteStudent(student)
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }
            }
        )
        
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }
    
    /**
     * Setup button listeners
     */
    private fun setupListeners() {
        binding.buttonAdd.setOnClickListener {
            addStudent()
        }
        
        binding.buttonClear.setOnClickListener {
            clearAllStudents()
        }
    }
    
    /**
     * Observe data (Core!)
     *
     * Most important part:
     * Flow publishes data, then
     * RecyclerView updates automatically
     */
    private fun observeStudents() {
        lifecycleScope.launch {
            // Start Flow subscription
            database.studentDao().getAllStudents().collect { students ->
                // Executes whenever data changes
                currentStudents = students
                adapter.updateList(students)
                binding.textViewCount.text = "Total: ${students.size}"
                Log.d(TAG, "Data updated: ${students.size} students")
            }
        }
    }
    
    /**
     * Add student (async)
     *
     * suspend function must be called with lifecycleScope.launch
     */
    private fun addStudent() {
        val name = binding.editTextStudent.text.toString().trim()
        
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Check for duplicates
        if (currentStudents.any { it.name == name }) {
            Toast.makeText(this, "Name already exists", Toast.LENGTH_SHORT).show()
            return
        }
        
        val student = Student(
            name = name,
            department = "CS",
            grade = "1st Year"
        )
        
        // Save to database asynchronously
        lifecycleScope.launch {
            database.studentDao().insertStudent(student)
            binding.editTextStudent.text.clear()
            Toast.makeText(this@MainActivity, "Added: $name", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Added: $name")
        }
    }
    
    /**
     * Delete student (async)
     */
    private fun deleteStudent(student: Student) {
        lifecycleScope.launch {
            database.studentDao().deleteStudent(student)
            Toast.makeText(this@MainActivity, "Deleted", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Deleted: ${student.name}")
        }
    }
    
    /**
     * Delete all students (async)
     */
    private fun clearAllStudents() {
        lifecycleScope.launch {
            database.studentDao().deleteAllStudents()
            Toast.makeText(this@MainActivity, "All cleared", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "All cleared")
        }
    }
}
```

## Build Configuration

### Required Dependencies (build.gradle.kts)

```kotlin
plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")  // Room annotation processor required
}

dependencies {
    // Room Database
    val roomVersion = "2.8.3"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Lifecycle (lifecycleScope required)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    
    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")
}
```

## Project Structure

```
AppWeek11/
├── app/src/main/java/com/appweek11/
│   ├── MainActivity.kt              # Main activity (business logic)
│   ├── StudentAdapter.kt            # RecyclerView adapter
│   ├── Student.kt                   # Entity (table definition)
│   ├── StudentDao.kt                # DAO (data access)
│   └── StudentDatabase.kt           # Database (Singleton)
├── app/src/main/res/layout/
│   ├── activity_main.xml            # Main layout
│   └── item_student.xml             # Item layout
└── app/build.gradle.kts             # Build configuration
```

## How to Run

1. Open project in Android Studio (`D:\kotlin-2025a\AppWeek11`)
2. Verify Room dependencies and KAPT plugin in `build.gradle`
3. Run Build > Rebuild Project
4. Build and run on emulator or device
5. Enter student name and click add button
6. Long click to delete
7. Restart app to verify data persistence

## Key Concepts Summary

### Flow vs. LiveData

Flow and LiveData are similar but different:

**Flow** (Kotlin Coroutines approach)
- More flexible
- Coroutine native
- Recommended for modern apps

```kotlin
database.studentDao().getAllStudents().collect { students ->
    adapter.updateList(students)
}
```

**LiveData** (androidx approach, legacy)
- Simpler
- Older approach

```kotlin
viewModel.students.observe(this) { students ->
    adapter.updateList(students)
}
```

### Suspend vs. Non-suspend

**Suspend function (runs in background)**
```kotlin
suspend fun insertStudent(student: Student)
// Must be called with lifecycleScope.launch
lifecycleScope.launch {
    database.studentDao().insertStudent(student)
}
```

**Regular function (runs immediately on main thread)**
```kotlin
fun getAllStudents(): Flow<List<Student>>  // Not suspend
// Can call directly
database.studentDao().getAllStudents()
```

## Week 10 vs Week 11 Comparison

| Item | Week 10 | Week 11 |
|------|---------|---------|
| **Focus** | Room basics | Flow + advanced async |
| **Data Updates** | Manual refresh | Automatic (Flow) |
| **Code Complexity** | Intermediate | Advanced |
| **Performance** | Basic | Optimized |
| **Real-time** | Requires reload | Instant updates |

## Common Mistakes and Solutions

### Mistake 1: Calling suspend function without lifecycleScope

```kotlin
// Bad: Compile error
database.studentDao().insertStudent(student)

// Good
lifecycleScope.launch {
    database.studentDao().insertStudent(student)
}
```

### Mistake 2: Not subscribing to Flow

```kotlin
// Bad: Does nothing
database.studentDao().getAllStudents()

// Good
lifecycleScope.launch {
    database.studentDao().getAllStudents().collect { students ->
        adapter.updateList(students)
    }
}
```

### Mistake 3: Creating database multiple times

```kotlin
// Bad: Creates new instance every time
val db = Room.databaseBuilder(context, StudentDatabase::class.java, "db").build()

// Good: Use Singleton
val db = StudentDatabase.getDatabase(context)
```

## Learning Outcomes

Completing Week 11:
- Understand Room Database structure
- Master Entity, DAO, Database pattern
- Implement automatic UI updates with Flow
- Use lifecycleScope and coroutines
- Understand safety of async data processing
- Implement Singleton pattern
- Apply basic data validation

## Resources

- [Room Persistence Library](https://developer.android.com/training/data-storage/room)
- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [Kotlin Flow Documentation](https://kotlinlang.org/docs/flow.html)

## Next Steps

Week 12 introduces **ViewModel** for improved UI state management.
