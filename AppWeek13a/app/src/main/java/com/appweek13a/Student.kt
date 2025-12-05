package com.appweek13a

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "student_name")
    val name: String,

    val department: String = "Computer Science",
    val grade: String = "1st Year"
)