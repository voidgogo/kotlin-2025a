package com.appweek13b

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

class MainActivity : ComponentActivity() {

    private val viewModel: StudentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val students by viewModel.students.collectAsState()

            StudentListScreen(
                students = students,
                onAddClick = { name ->
                    viewModel.addStudent(name)
                },
                onDeleteClick = { student ->
                    viewModel.deleteStudent(student)
                }
            )
        }
    }
}
