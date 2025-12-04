# Mobile Programming, Fall 2025 Semester

# Week 12: UI State Management with ViewModel and StateFlow

This document covers Week 12 of the Kotlin learning project. Building on Week 11's Room Database and Flow, this week teaches systematic UI state management using **ViewModel**. This week uses a simple **Counter example** to clearly understand ViewModel concepts.

## Project Overview
- **Path**: `D:\kotlin-2025a\AppWeek12`
- **Environment**: Android Studio, Kotlin, XML Views (Empty Views Activity)
- **Purpose**: UI state management with ViewModel and StateFlow
- **Structure**: Single Activity + ViewModel + StateFlow
- **Key Concepts**: ViewModel, StateFlow, lifecycleScope, repeatOnLifecycle

## Week 12 Objectives

- Understand ViewModel concept and role
- Manage UI state with StateFlow
- Observe data in a lifecycle-aware manner
- Preserve data during Configuration Change
- Master simple yet powerful architecture pattern
- Display UI state changes (color changes, etc.)

## Project Content

- **Project**: `AppWeek12` (ViewModel + StateFlow)
- **Goal**: Simple counter app (increment, decrement, reset)
- **Core Components**:
    - `CounterViewModel.kt`: State management logic
    - `MainActivity.kt`: UI layer
    - `activity_main.xml`: Layout

## What is ViewModel?

### ViewModel's Role

**ViewModel** is a class that manages UI state. Data persists even when screen rotates.

```
App Launch
  ↓
Activity Creation → ViewModel Creation
  ↓
Screen Rotation (Configuration Change)
  ↓
Activity Recreated (onCreate called again)
  ↓
Regular variables: Values reset
ViewModel: Values preserved (not recreated)
```

### ViewModel Characteristics

- **Lifecycle Aware**: Follows Activity/Fragment lifecycle
- **Survives Configuration Change**: Data persists on screen rotation
- **Memory Efficient**: Created when needed, cleaned up when no longer needed
- **Separated from UI**: Separates business logic from UI logic

## What is StateFlow?

**StateFlow** is a Flow that always maintains current state.

```kotlin
// Analogy: Radio channel (Flow) vs Switch state (StateFlow)
// - Flow: Just keep receiving signals
// - StateFlow: Remembers current state

val _count = MutableStateFlow(0)  // Current state: 0
val count: StateFlow<Int> = _count.asStateFlow()  // Read-only
```

## Code Implementation

### 1. CounterViewModel (State Management)

```kotlin
package com.appweek12

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.ViewModel

/**
 * CounterViewModel
 * 
 * Role:
 * - Manage counter state (_count)
 * - Provide state change functions (increment, decrement, reset, incrementBy10)
 * - Exist independently of Activity
 * 
 * Characteristics:
 * - Inherits from ViewModel (lifecycle management)
 * - Data preserved when Activity recreated
 * - No UI logic (only business logic)
 */
class CounterViewModel : ViewModel() {
    
    // Private mutable state
    // Only ViewModel can modify
    private val _count = MutableStateFlow(0)
    
    // Public immutable state
    // Activity can only read
    val count: StateFlow<Int> = _count.asStateFlow()
    
    /**
     * Increment by 1
     */
    fun increment() {
        _count.value += 1
    }
    
    /**
     * Decrement by 1
     */
    fun decrement() {
        _count.value -= 1
    }
    
    /**
     * Reset to 0
     */
    fun reset() {
        _count.value = 0
    }
    
    /**
     * Increment by 10
     */
    fun incrementBy10() {
        _count.value = (_count.value) + 10
    }
}
```

**Key Points**:
- `MutableStateFlow`: Writable state
- `StateFlow`: Read-only state
- `asStateFlow()`: Convert MutableStateFlow to StateFlow
- ViewModel has no UI logic

### 2. MainActivity (UI Layer)

```kotlin
package com.appweek12

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.appweek12.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    // View Binding
    private lateinit var binding: ActivityMainBinding
    
    // Initialize ViewModel
    // by viewModels(): Automatically creates ViewModel and manages lifecycle
    private val viewModel: CounterViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Start observing state
        setupObservers()
        
        // Setup button listeners
        setupListeners()
    }
    
    /**
     * Observe StateFlow
     * 
     * UI updates whenever ViewModel state changes
     */
    private fun setupObservers() {
        lifecycleScope.launch {
            // repeatOnLifecycle: Automatically follow Activity lifecycle
            // Observe only when STARTED state
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.count.collect { count ->
                    // Executes whenever count changes
                    binding.textViewCount.text = count.toString()
                    
                    // Change color based on state
                    when {
                        count > 0 -> binding.textViewCount.setTextColor(Color.BLUE)    // Positive: Blue
                        count < 0 -> binding.textViewCount.setTextColor(Color.RED)     // Negative: Red
                        else -> binding.textViewCount.setTextColor(Color.BLACK)        // Zero: Black
                    }
                }
            }
        }
    }
    
    /**
     * Setup button click listeners
     * 
     * Button click → Call ViewModel method → State change → UI auto-update
     */
    private fun setupListeners() {
        // +1 button
        binding.buttonPlus.setOnClickListener {
            viewModel.increment()
        }
        
        // -1 button
        binding.buttonMinus.setOnClickListener {
            viewModel.decrement()
        }
        
        // Reset button
        binding.buttonReset.setOnClickListener {
            viewModel.reset()
        }
        
        // +10 button
        binding.buttonPlus10.setOnClickListener {
            viewModel.incrementBy10()
        }
    }
}
```

**Key Points**:
- `by viewModels()`: Automatic ViewModel creation
- `repeatOnLifecycle()`: Automatically follow lifecycle
- `collect { }`: Observe StateFlow values
- Button click → ViewModel method → State change → UI update

### 3. activity_main.xml (Layout)

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:gravity="center"
    android:padding="20dp"
    tools:context=".MainActivity">

    <!-- Counter display -->
    <TextView
        android:id="@+id/textViewCount"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="0"
        android:textSize="60sp"
        android:textStyle="bold"
        android:layout_marginBottom="40dp"
        android:textColor="@android:color/black" />

    <!-- Button group -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:gravity="center"
        android:spacing="10dp">

        <!-- -1 button -->
        <Button
            android:id="@+id/buttonMinus"
            android:layout_width="80dp"
            android:layout_height="60dp"
            android:text="-1"
            android:textSize="20sp"
            android:layout_margin="5dp" />

        <!-- +1 button -->
        <Button
            android:id="@+id/buttonPlus"
            android:layout_width="80dp"
            android:layout_height="60dp"
            android:text="+1"
            android:textSize="20sp"
            android:layout_margin="5dp" />

        <!-- +10 button -->
        <Button
            android:id="@+id/buttonPlus10"
            android:layout_width="80dp"
            android:layout_height="60dp"
            android:text="+10"
            android:textSize="20sp"
            android:layout_margin="5dp" />

    </LinearLayout>

    <!-- Reset button -->
    <Button
        android:id="@+id/buttonReset"
        android:layout_width="200dp"
        android:layout_height="50dp"
        android:text="Reset"
        android:textSize="18sp"
        android:layout_marginTop="40dp" />

</LinearLayout>
```

## Build Configuration

### Required Dependencies (build.gradle.kts)

```kotlin
plugins {
    id("com.android.application")
    id("kotlin-android")
}

dependencies {
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    
    // Coroutines & Flow (StateFlow)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Lifecycle (lifecycleScope)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
}
```

**Note**: Using `viewModels()` might also require Activity/Fragment dependencies:

```kotlin
implementation("androidx.activity:activity-ktx:1.8.0")
implementation("androidx.fragment:fragment-ktx:1.6.0")
```

## Project Structure

```
AppWeek12/
├── app/src/main/java/com/appweek12/
│   ├── MainActivity.kt              # UI layer
│   └── CounterViewModel.kt          # State management
├── app/src/main/res/layout/
│   └── activity_main.xml            # Layout
└── app/build.gradle.kts             # Build configuration
```

## How to Run

1. Open project in Android Studio (`D:\kotlin-2025a\AppWeek12`)
2. Verify required dependencies in `build.gradle`
3. Run Build > Rebuild Project
4. Build and run on emulator or device
5. Click buttons to change numbers
6. Rotate screen to verify number is preserved (ViewModel effect)

## Key Concepts Summary

### ViewModel Lifecycle

```
Activity Creation (onCreate)
     ↓
ViewModel Creation (first time only)
     ↓
Screen Rotation (Configuration Change)
     ↓
Activity Recreated (onCreate called again)
     ↓
ViewModel not recreated (data preserved!)
     ↓
Activity Destroyed
     ↓
ViewModel Cleaned (onCleared)
```

### StateFlow Usage Pattern

```kotlin
// 1. Define state in ViewModel
private val _count = MutableStateFlow(0)
val count: StateFlow<Int> = _count.asStateFlow()

// 2. Change state in ViewModel
fun increment() {
    _count.value += 1
}

// 3. Observe state in Activity
lifecycleScope.launch {
    viewModel.count.collect { count ->
        // Update UI
    }
}

// 4. Request state change in Activity
viewModel.increment()
```

### Why repeatOnLifecycle is Needed

```kotlin
// Bad: Continues observing even after Activity destroyed
lifecycleScope.launch {
    viewModel.count.collect { count ->
        binding.textViewCount.text = count.toString()
    }
}

// Good: Observes only when STARTED state
lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.count.collect { count ->
            binding.textViewCount.text = count.toString()
        }
    }
}
```

## Week 11 vs Week 12 Comparison

| Item | Week 11 | Week 12 |
|------|---------|---------|
| **Main Technology** | Room + Flow | ViewModel + StateFlow |
| **Data Source** | Database | Memory (state) |
| **Storage** | Permanent | Temporary (app runtime) |
| **UI Update** | On data change | On state change |
| **Configuration Change** | Requires special handling | Auto-preserved |
| **Complexity** | Intermediate | Low (learning) |
| **Real-world Use** | Example: Student DB | Example: Counter, UI state |

## Common Mistakes and Solutions

### Mistake 1: ViewModel holding Context reference

```kotlin
// Bad: ViewModel references Activity (memory leak)
class BadViewModel(val activity: MainActivity) : ViewModel() {
    // Memory leak risk!
}

// Good: Pure logic without context
class GoodViewModel : ViewModel() {
    fun increment() {
        _count.value += 1  // Only change state, independent of UI
    }
}
```

### Mistake 2: Not using repeatOnLifecycle in collect

```kotlin
// Bad: Possible memory leak
lifecycleScope.launch {
    viewModel.count.collect { count ->
        binding.textViewCount.text = count.toString()
    }
}

// Good: Auto-cancel based on lifecycle
lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.count.collect { count ->
            binding.textViewCount.text = count.toString()
        }
    }
}
```

### Mistake 3: Recreating ViewModel every time

```kotlin
// Bad: Creates new ViewModel in onCreate (recreated every time)
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val viewModel = CounterViewModel()  // New instance every time!
}

// Good: Use viewModels()
private val viewModel: CounterViewModel by viewModels()
```

## Learning Outcomes

Completing Week 12:
- Understand ViewModel concept and role
- Manage UI state with StateFlow
- Observe data in lifecycle-aware manner
- Preserve data during configuration changes
- Separate business logic from UI logic
- Build simple, powerful architecture
- Develop apps where data persists after rotation

## Screen Rotation Test

To verify ViewModel's effectiveness:

1. Run app and click buttons to change counter
2. Rotate screen (turn device sideways)
3. Verify counter value is preserved
4. Without ViewModel, it would reset to 0

## Resources

- [ViewModel Overview](https://developer.android.com/topic/libraries/architecture/viewmodel)
- [StateFlow and SharedFlow](https://kotlinlang.org/docs/flow.html#stateflow-and-sharedflow)
- [Lifecycle Aware Components](https://developer.android.com/topic/libraries/architecture/lifecycle)
- [Coroutines Best Practices](https://developer.android.com/kotlin/coroutines/coroutines-best-practices)

## Next Steps

After completing Week 12, you've mastered ViewModel and StateFlow basics. Next:

- **Week 13+**: Combine Room Database with ViewModel (Student Manager App)
- **Week 13+**: Repository pattern for data abstraction
- **Week 13+**: Retrofit for network integration
