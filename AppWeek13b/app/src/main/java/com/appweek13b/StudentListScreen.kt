package com.appweek13b

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign

@Composable
fun StudentItem(
    student: Student,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = student.name,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "${student.department} • ${student.grade}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Button(
                onClick = onDeleteClick,
                modifier = Modifier.size(40.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF4444)
                )
            ) {
                Text("×", fontSize = 20.sp, color = Color.White)
            }
        }
    }
}

@Composable
fun StudentListScreen(
    students: List<Student>,
    onAddClick: (String) -> Unit,
    onDeleteClick: (Student) -> Unit
) {
    var studentName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Student Manager",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = studentName,
                onValueChange = { studentName = it },
                label = { Text("Student name") },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                singleLine = true
            )
            Button(
                onClick = {
                    onAddClick(studentName)
                    studentName = ""
                },
                modifier = Modifier.height(56.dp)
            ) {
                Text("Add")
            }
        }

        Text(
            text = "Total: ${students.size}",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (students.isEmpty()) {
            Text(
                text = "No students yet. Add one!",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                textAlign = TextAlign.Center
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(students) { student ->
                    StudentItem(
                        student = student,
                        onDeleteClick = { onDeleteClick(student) }
                    )
                }
            }
        }
    }
}