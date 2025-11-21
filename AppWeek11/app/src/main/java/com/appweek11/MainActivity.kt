package com.appweek11

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.appweek11.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: StudentDatabase
    private lateinit var adapter: StudentAdapter
    private var currentStudents: List<Student> = emptyList()

    companion object {
        const val TAG = "AppWeek11"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "App started")

        // Room Database 초기화
        database = StudentDatabase.getDatabase(this)

        // RecyclerView 설정
        setupRecyclerView()

        // 버튼 리스너 설정
        setupListeners()

        // 데이터 관찰 시작 (중요!)
        observeStudents()
    }

    /**
     * RecyclerView 기본 설정
     */
    private fun setupRecyclerView() {
        adapter = StudentAdapter(
            emptyList(),
            onItemClick = { student, _ ->
                // 아이템 클릭 → 상세정보 표시
                AlertDialog.Builder(this)
                    .setTitle(student.name)
                    .setMessage("${student.department} ${student.grade}")
                    .setPositiveButton("OK", null)
                    .show()
                Log.d(TAG, "Clicked: ${student.name}")
            },
            onItemLongClick = { position ->
                // 롱클릭 → 삭제 확인
                if (position < currentStudents.size) {
                    val student = currentStudents[position]
                    AlertDialog.Builder(this)
                        .setTitle("Delete?")
                        .setMessage(student.name)
                        .setPositiveButton("Delete") { _, _ ->
                            deleteStudent(student)
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    /**
     * 버튼 리스너 설정
     */
    private fun setupListeners() {
        binding.buttonAdd.setOnClickListener {
            addStudent()
        }

        binding.buttonClear.setOnClickListener {
            clearAllStudents()
        }
    }

    /**
     * 데이터 관찰 (핵심!)
     *
     * 가장 중요
     * Flow가 발행하는 데이터를 받아서
     * RecyclerView를 자동으로 업데이트
     */
    private fun observeStudents() {
        lifecycleScope.launch {
            // Flow 구독 시작
            database.studentDao().getAllStudents().collect { students ->
                // 데이터 변할 때마다 여기 실행됨
                currentStudents = students
                adapter.updateList(students)
                binding.textViewCount.text = "Total: ${students.size}"
                Log.d(TAG, "Data updated: ${students.size} students")
            }
        }
    }

    /**
     * 학생 추가 (비동기)
     *
     * suspend 함수이므로 반드시 lifecycleScope.launch로 호출
     */
    private fun addStudent() {
        val name = binding.editTextStudent.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "이름을 입력하세요", Toast.LENGTH_SHORT).show()
            return
        }

        // 중복 체크
        if (currentStudents.any { it.name == name }) {
            Toast.makeText(this, "이미 있는 이름입니다", Toast.LENGTH_SHORT).show()
            return
        }

        val student = Student(
            name = name,
            department = "CS",
            grade = "1st Year"
        )

        // 비동기로 데이터베이스에 저장
        lifecycleScope.launch {
            database.studentDao().insertStudent(student)
            binding.editTextStudent.text.clear()
            Toast.makeText(this@MainActivity, "Added: $name", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Added: $name")
        }
    }

    /**
     * 학생 삭제 (비동기)
     */
    private fun deleteStudent(student: Student) {
        lifecycleScope.launch {
            database.studentDao().deleteStudent(student)
            Toast.makeText(this@MainActivity, "Deleted", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Deleted: ${student.name}")
        }
    }

    /**
     * 전체 삭제 (비동기)
     */
    private fun clearAllStudents() {
        lifecycleScope.launch {
            database.studentDao().deleteAllStudents()
            Toast.makeText(this@MainActivity, "All cleared", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "All cleared")
        }
    }
}