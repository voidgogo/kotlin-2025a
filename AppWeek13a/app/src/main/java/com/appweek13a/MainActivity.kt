package com.appweek13a

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.appweek13a.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: StudentViewModel by viewModels()
    private lateinit var adapter: StudentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAdapter()
        setupObservers()
        setupListeners()
    }

    private fun setupAdapter() {
        adapter = StudentAdapter { student ->
            viewModel.deleteStudent(student)
        }
        binding.recyclerViewStudents.adapter = adapter
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.students.collect { students ->
                    adapter.submitList(students)
                    binding.textViewCount.text = "Total: ${students.size}"
                }
            }
        }
    }

    private fun setupListeners() {
        binding.buttonAdd.setOnClickListener {
            val name = binding.editTextName.text.toString()
            viewModel.addStudent(name)
            binding.editTextName.text.clear()
        }
    }
}