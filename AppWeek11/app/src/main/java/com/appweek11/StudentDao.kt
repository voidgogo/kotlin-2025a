package com.appweek11

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * StudentDao (Data Access Object)
 *
 * @Dao: 데이터 접근 인터페이스
 * 여기서만 SELECT, INSERT, DELETE 등을 정의
 */
@Dao
interface StudentDao {

    /**
     * 모든 학생 조회 (Flow로 자동 업데이트)
     *
     * Flow란?
     * - 데이터가 변하면 자동으로 알려줌
     * - 마치 라디오 채널을 계속 듣는 것처럼
     * - UI가 자동으로 업데이트됨
     */
    @Query("SELECT * FROM students ORDER BY id DESC")
    fun getAllStudents(): Flow<List<Student>>

    /**
     * 학생 추가
     *
     * suspend란?
     * - 이 함수가 오래 걸릴 수 있다는 뜻
     * - 백그라운드에서 실행
     * - UI가 멈추지 않음
     *
     * @Insert
     * - "이 객체를 데이터베이스에 추가"
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student)

    /**
     * 특정 학생 삭제
     *
     * @Delete
     * - "이 객체를 데이터베이스에서 제거"
     */
    @Delete
    suspend fun deleteStudent(student: Student)

    /**
     * 모든 학생 삭제
     *
     * @Query
     * - 사용자 정의 SQL 쿼리
     * - DELETE FROM students: "students 테이블 전체 삭제"
     */
    @Query("DELETE FROM students")
    suspend fun deleteAllStudents()
}