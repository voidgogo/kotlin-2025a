package com.appweek12

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.appweek12.databinding.ActivityMainBinding

import androidx.activity.viewModels

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    /**
     * by viewModels(): ViewModel을 자동 관리
     * 화면 회전해도 ViewModel은 살아있음
     */
    private val viewModel: CounterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // onSaveInstanceState 필요 없음!
        // ViewModel이 자동으로 처리

        setupObservers()
        setupListeners()
    }

    /**
     * LiveData 관찰
     * count가 변할 때마다 자동으로 UI 업데이트
     */
    private fun setupObservers() {
        viewModel.count.observe(this) { count ->
            binding.textViewCount.text = count.toString()

            when {
                count > 0 -> binding.textViewCount.setTextColor(Color.GREEN)
                count < 0 -> binding.textViewCount.setTextColor(Color.RED)
                else -> binding.textViewCount.setTextColor(Color.BLACK)
            }
        }
    }

    /**
     * 버튼 리스너
     * ViewModel의 메소드만 호출
     * UI 업데이트는 observe에서 처리
     */
    private fun setupListeners() {
        binding.buttonPlus.setOnClickListener {
            viewModel.increment()
            // updateCountDisplay() 호출 필요 없음!
            // observe가 자동으로 처리
        }

        binding.buttonMinus.setOnClickListener {
            viewModel.decrement()
        }

        binding.buttonReset.setOnClickListener {
            viewModel.reset()
        }

        binding.buttonPlus10.setOnClickListener {
            viewModel.incrementBy10()
        }
    }
}
