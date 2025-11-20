# Mobile Programming, Fall 2025 Semester

This repository contains the source code and documentation for Week 10 of the Kotlin learning project, developed in Android Studio using XML Views. The focus is on Room Database integration, asynchronous data operations with Coroutines, and real-time data observation with Flow.

## Project Overview
- **Path**: `D:\kotlin-2025a\AppWeek10`
- **Environment**: Android Studio, Kotlin, XML Views (Empty Views Activity)
- **Purpose**: Integrate Room Database for persistent data storage, implement asynchronous database operations with Coroutines, and apply Flow for reactive data updates in RecyclerView.
- **Structure**: Single Activity (`MainActivity.kt`) with Room Database, RecyclerView displaying real-time data, and asynchronous CRUD operations.

## Week 10: Room Database Integration and Asynchronous Data Operations

### Objectives
- Understand Room Database architecture (Entity, DAO, Database).
- Implement data persistence that survives app restarts.
- Master Kotlin Coroutines for asynchronous database operations.
- Apply Flow for reactive, real-time data observation.
- Integrate Room Database with RecyclerView for dynamic UI updates.
- Optimize database queries and transactions for performance.

### Content
- **Project**: `AppWeek10` (Room Database + RecyclerView).
- **Main Focus**: Persistent Student Manager with real-time database synchronization.
- **Key Components**:
    - `Student.kt` (Entity): Table schema definition with annotations
    - `StudentDao.kt` (DAO): Data Access Object with CRUD operations
    - `StudentDatabase.kt`: Singleton database instance provider
    - `MainActivity.kt`: Activity with Flow-based data observation
    - XML Layouts: activity_main.xml and item_student.xml

### Room Database Architecture

#### Three Core Components
```
Room Database Architecture
├── Entity (Table Schema)
│   ├── @Entity annotation
│   ├── @PrimaryKey for unique identification
│   └── @ColumnInfo for column naming
├── DAO (Data Access Object)
│   ├── @Query for SELECT operations
│   ├── @Insert for INSERT operations
│   ├── @Update for UPDATE operations
│   ├── @Delete for DELETE operations
│   └── Flow<> for reactive data streams
└── Database (Database Holder)
    ├── @Database annotation
    ├── Singleton pattern implementation
    └── DAO provider methods
```

### Key Features

#### 1. Data Persistence
- **Persistent Storage**: Student data survives app restarts
- **SQLite Backend**: Room provides abstraction over SQLite
- **Automatic Schema Management**: Room handles table creation/migration
- **Type Conversion**: Automatic data type conversion

#### 2. Asynchronous Operations with Coroutines
- **Suspend Functions**: All write operations use `suspend` modifier
- **Background Threads**: Database operations run off main thread
- **Structured Concurrency**: Coroutines tied to lifecycle scope
- **Exception Handling**: Proper error handling with try-catch

#### 3. Real-time Data with Flow
- **Reactive Updates**: UI automatically updates when data changes
- **Flow Emission**: DAO returns Flow<List<Student>>
- **Automatic Collection**: lifecycleScope.launch collects data
- **No Manual Refresh**: RecyclerView updates automatically

#### 4. CRUD Operations
- **Create**: Add new students with auto-generated IDs
- **Read**: Query all students with Flow observation
- **Update**: Modify existing student information
- **Delete**: Remove individual or all students
- **Search**: Query students by name or department

#### 5. Database Inspector Integration
- **Live Database View**: View database in Android Studio
- **Query Execution**: Run SQL queries directly
- **Data Inspection**: Examine table contents and schema
- **Debugging Support**: Track database operations

### How to Run
1. Open the project in Android Studio (`D:\kotlin-2025a\AppWeek10`).
2. Ensure `build.gradle` has Room dependencies and KAPT plugin enabled.
3. Add Room, Coroutines, and Lifecycle dependencies.
4. Perform Gradle sync (File > Sync Now).
5. Build and run on an emulator or device (API 24+ recommended).
6. Add students and verify data persists after app restart.
7. Use Database Inspector (View > Tool Windows > App Inspection) to view database.

### Sample Data Structures

#### 1. Student Entity (Table Definition)
```kotlin
@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    @ColumnInfo(name = "student_name")
    val name: String,
    
    val department: String = "Computer Science",
    val grade: String = "1st Year",
    val email: String = "",
    
    @ColumnInfo(name = "added_date")
    val addedDate: Long = System.currentTimeMillis()
) {
    override fun toString(): String = name
}
```

**Annotations Explained**:
- `@Entity`: Marks class as database table
- `@PrimaryKey(autoGenerate = true)`: Auto-incrementing primary key
- `@ColumnInfo`: Custom column name (optional)

#### 2. StudentDao (Data Access Object)
```kotlin
@Dao
interface StudentDao {
    
    // Query all students with Flow (reactive)
    @Query("SELECT * FROM students ORDER BY id DESC")
    fun getAllStudents(): Flow<List<Student>>
    
    // Insert student (suspend for async)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student)
    
    // Insert multiple students
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllStudents(students: List<Student>)
    
    // Update student
    @Update
    suspend fun updateStudent(student: Student)
    
    // Delete specific student
    @Delete
    suspend fun deleteStudent(student: Student)
    
    // Delete all students
    @Query("DELETE FROM students")
    suspend fun deleteAllStudents()
    
    // Search students by name
    @Query("SELECT * FROM students WHERE student_name LIKE '%' || :searchQuery || '%' ORDER BY id DESC")
    fun searchStudents(searchQuery: String): Flow<List<Student>>
    
    // Get student count
    @Query("SELECT COUNT(*) FROM students")
    fun getStudentCount(): Flow<Int>
    
    // Get students by department
    @Query("SELECT * FROM students WHERE department = :department")
    fun getStudentsByDepartment(department: String): Flow<List<Student>>
}
```

**Key Points**:
- `Flow<>`: Reactive data stream that emits updates
- `suspend`: Marks function as coroutine-compatible
- `OnConflictStrategy.REPLACE`: Replace on duplicate key

#### 3. StudentDatabase (Singleton Pattern)
```kotlin
@Database(entities = [Student::class], version = 1, exportSchema = false)
abstract class StudentDatabase : RoomDatabase() {
    
    abstract fun studentDao(): StudentDao
    
    companion object {
        @Volatile
        private var INSTANCE: StudentDatabase? = null
        
        fun getDatabase(context: Context): StudentDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StudentDatabase::class.java,
                    "student_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
```

**Singleton Pattern Breakdown**:
- `@Volatile`: Ensures visibility across threads
- `synchronized(this)`: Thread-safe instance creation
- `INSTANCE ?:`: Returns existing or creates new instance
- `context.applicationContext`: App-level context for lifecycle

#### 4. MainActivity Integration
```kotlin
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: StudentDatabase
    private lateinit var adapter: StudentAdapter
    private var currentStudents: List<Student> = emptyList()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Initialize Room Database
        database = StudentDatabase.getDatabase(this)
        
        setupRecyclerView()
        setupListeners()
        observeStudents()  // Key: Start observing database
    }
    
    /**
     * Observe students from database with Flow
     * Automatically updates RecyclerView when data changes
     */
    private fun observeStudents() {
        lifecycleScope.launch {
            database.studentDao().getAllStudents().collect { students ->
                currentStudents = students
                adapter.updateList(students)
                updateStudentCount()
                Log.d(TAG, "Students updated: ${students.size}")
            }
        }
    }
    
    /**
     * Add student to database (asynchronous)
     */
    private fun addStudent() {
        val studentName = binding.editTextStudent.text.toString().trim()
        
        if (studentName.isEmpty()) {
            showToast("Please enter a student name")
            return
        }
        
        // Check for duplicates
        if (currentStudents.any { it.name == studentName }) {
            showToast("Student '$studentName' already exists")
            return
        }
        
        val newStudent = Student(
            name = studentName,
            department = "Computer Science",
            grade = "Year ${(1..4).random()}",
            email = "${studentName.replace(" ", ".")}@university.edu"
        )
        
        // Insert asynchronously with coroutine
        lifecycleScope.launch {
            try {
                database.studentDao().insertStudent(newStudent)
                binding.editTextStudent.text.clear()
                showToast("Added: $studentName")
                Log.d(TAG, "Added student: $studentName")
            } catch (e: Exception) {
                showToast("Error adding student: ${e.message}")
                Log.e(TAG, "Error adding student", e)
            }
        }
    }
    
    /**
     * Delete student from database (asynchronous)
     */
    private fun removeStudent(student: Student) {
        lifecycleScope.launch {
            try {
                database.studentDao().deleteStudent(student)
                showToast("Removed: ${student.name}")
                Log.d(TAG, "Removed student: ${student.name}")
            } catch (e: Exception) {
                showToast("Error removing student: ${e.message}")
                Log.e(TAG, "Error removing student", e)
            }
        }
    }
    
    /**
     * Clear all students from database (asynchronous)
     */
    private fun clearAllStudents() {
        if (currentStudents.isEmpty()) {
            showToast("List is already empty")
            return
        }
        
        val count = currentStudents.size
        lifecycleScope.launch {
            try {
                database.studentDao().deleteAllStudents()
                showToast("Cleared all $count students")
                Log.d(TAG, "Cleared all students")
            } catch (e: Exception) {
                showToast("Error clearing students: ${e.message}")
                Log.e(TAG, "Error clearing students", e)
            }
        }
    }
}
```

### Coroutines and Flow Concepts

#### What is a Coroutine?
- **Lightweight Thread**: Efficient asynchronous programming
- **Structured Concurrency**: Tied to lifecycle (lifecycleScope)
- **Non-blocking**: Doesn't block main thread
- **Exception Handling**: Proper error propagation

#### What is Flow?
- **Reactive Stream**: Emits values over time
- **Cold Stream**: Only activates when collected
- **Lifecycle Aware**: Automatically cancels on lifecycle end
- **Backpressure**: Handles data emission rate

#### Why Use Suspend Functions?
```kotlin
// BAD: Blocking main thread (UI freezes)
fun addStudentBlocking(student: Student) {
    database.studentDao().insertStudent(student)  // Blocks UI!
}

// GOOD: Non-blocking with coroutine
suspend fun addStudent(student: Student) {
    database.studentDao().insertStudent(student)  // Runs on background
}
```

### Build Configuration

#### Required Dependencies (build.gradle.kts)
```kotlin
plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")  // Required for Room annotation processing
}

dependencies {
    // Room Database
    val roomVersion = "2.8.3"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Lifecycle (for lifecycleScope)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
}
```

### Project Structure
```
AppWeek10/
├── app/src/main/java/com/appweek10/
│   ├── MainActivity.kt                  # Activity with Flow observation
│   ├── StudentAdapter.kt                # RecyclerView Adapter
│   └── data/
│       ├── Student.kt                   # Entity (Table Schema)
│       ├── StudentDao.kt                # DAO (Data Access)
│       └── StudentDatabase.kt           # Database Singleton
├── app/src/main/res/layout/
│   ├── activity_main.xml                # Main Layout
│   └── item_student.xml                 # Item Layout
└── app/build.gradle.kts                 # Build Configuration
```

### Learning Outcomes
By completing Week 10, students will:
- Master Room Database three-component architecture
- Implement persistent data storage in Android apps
- Use Kotlin Coroutines for asynchronous operations
- Apply Flow for reactive data observation
- Integrate Room with RecyclerView seamlessly

### Comparison: Week 09 vs Week 10

| Aspect | Week 09 (RecyclerView)        | Week 10 (Room + RecyclerView)  |
|--------|-------------------------------|--------------------------------|
| **Data Storage** | ArrayList (memory only)       | Room Database (persistent)   |
| **Data Lifetime** | Lost on app restart           | Survives app restarts        |
| **Data Operations** | Direct list manipulation      | Async database operations    |
| **UI Updates** | Manual notifyDataSetChanged() | Automatic with Flow          |
| **Performance** | Good for small datasets       | Optimized for large datasets |
| **Search/Filter** | In-memory filtering           | SQL queries                  |
| **Real-world Usage** | Demo/prototype apps           | Production apps              |

### Common Pitfalls and Solutions

#### Issue: App crashes with "Cannot access database on main thread"
**Solution**:
```kotlin
// BAD: Blocking main thread
fun addStudent(student: Student) {
    database.studentDao().insertStudent(student)  // Crash!
}

// GOOD: Use coroutine with suspend
fun addStudent(student: Student) {
    lifecycleScope.launch {
        database.studentDao().insertStudent(student)  // Safe
    }
}
```

#### Issue: Flow not updating RecyclerView
**Solution**:
```kotlin
// Ensure you're collecting Flow in lifecycleScope
lifecycleScope.launch {
    database.studentDao().getAllStudents().collect { students ->
        adapter.updateList(students)  // Must update adapter
    }
}
```

#### Issue: Duplicate database instances
**Solution**:
```kotlin
// Always use getDatabase() method (Singleton pattern)
val database = StudentDatabase.getDatabase(context)
// NOT: StudentDatabase()  // Don't call constructor directly
```

### Resources

#### Official Documentation
- [Room Persistence Library](https://developer.android.com/training/data-storage/room)
- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [Kotlin Flow Documentation](https://kotlinlang.org/docs/flow.html)

#### Related Topics
- Week 09: RecyclerView Fundamentals

### Final Notes

Room Database is the **Android Jetpack standard** for local data persistence. Combined with Coroutines and Flow, it provides a powerful, efficient, and type-safe way to manage app data. 

