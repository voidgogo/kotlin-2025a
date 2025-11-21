package com.appweek11

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * StudentDatabase
 *
 * @Database: 데이터베이스 정의
 * entities: 이 데이터베이스에 포함된 테이블들
 * version: 데이터베이스 버전 (나중에 변경시 필요)
 */
@Database(entities = [Student::class], version = 1, exportSchema = false)
abstract class StudentDatabase : RoomDatabase() {

    // 이 데이터베이스의 DAO를 제공하는 메소드
    abstract fun studentDao(): StudentDao

    companion object {
        /**
         * Singleton 패턴
         *
         * "데이터베이스는 앱 전체에서 딱 하나만 있어야 한다"
         * - 여러 개 만들면 데이터가 꼬임
         * - 따라서 getDatabase()를 사용해서 항상 같은 인스턴스 사용
         */

        @Volatile  // 멀티스레드 환경에서 안전하게 읽기
        private var INSTANCE: StudentDatabase? = null

        fun getDatabase(context: Context): StudentDatabase {
            // 이미 있으면 그거 사용
            if (INSTANCE != null) {
                return INSTANCE!!
            }

            // 없으면 새로 만들기 (이 부분은 한 번만 실행)
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StudentDatabase::class.java,
                    "student_database"  // 데이터베이스 파일 이름
                ).build()

                INSTANCE = instance
                return instance
            }
        }
    }
}