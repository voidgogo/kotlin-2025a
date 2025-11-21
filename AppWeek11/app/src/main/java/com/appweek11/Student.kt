package com.appweek11

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Student Entity
 *
 * @Entity: 데이터베이스 테이블
 * tableName: 테이블 이름은 "students"
 */
@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,  // 자동 증가하는 ID

    @ColumnInfo(name = "student_name")
    val name: String,  // 학생 이름

    val department: String = "Computer Science",  // 학과
    val grade: String = "1st Year"  // 학년
)