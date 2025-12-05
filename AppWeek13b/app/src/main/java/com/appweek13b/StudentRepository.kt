package com.appweek13b

import kotlinx.coroutines.flow.Flow

class StudentRepository(private val studentDao: StudentDao) {

    fun getAllStudents(): Flow<List<Student>> {
        return studentDao.getAllStudents()
    }

    suspend fun addStudent(name: String, department: String = "Computer Science", grade: String = "1st Year") {
        val student = Student(
            name = name,
            department = department,
            grade = grade
        )
        studentDao.insertStudent(student)
    }

    suspend fun deleteStudent(student: Student) {
        studentDao.deleteStudent(student)
    }

    suspend fun deleteAllStudents() {
        studentDao.deleteAllStudents()
    }
}