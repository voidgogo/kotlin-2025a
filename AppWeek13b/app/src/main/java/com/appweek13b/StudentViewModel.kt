package com.appweek13b

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StudentRepository(
        StudentDatabase.getDatabase(application).studentDao()
    )

    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> = _students.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllStudents().collect { list ->
                _students.value = list
            }
        }
    }

    fun addStudent(name: String) {
        if (name.isBlank()) return

        viewModelScope.launch {
            repository.addStudent(name)
        }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            repository.deleteStudent(student)
        }
    }

    fun deleteAllStudents() {
        viewModelScope.launch {
            repository.deleteAllStudents()
        }
    }
}