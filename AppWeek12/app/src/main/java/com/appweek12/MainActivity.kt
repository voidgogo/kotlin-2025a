package com.appweek12

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.appweek12.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    /**
     * 문제: Activity에 count를 직접 선언
     * 화면 회전하면 onCreate가 다시 호출되고 count = 0으로 초기화됨!
     */
    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.buttonPlus.setOnClickListener {
            count++
            updateCountDisplay()  // 매번 수동으로 UI 업데이트
        }

        binding.buttonMinus.setOnClickListener {
            count--
            updateCountDisplay()
        }

        binding.buttonReset.setOnClickListener {
            count = 0
            updateCountDisplay()
        }

        binding.buttonPlus10.setOnClickListener {
            count += 10
            updateCountDisplay()
        }
    }

    private fun updateCountDisplay() {
        binding.textViewCount.text = count.toString()

        when {
            count > 0 -> binding.textViewCount.setTextColor(Color.GREEN)
            count < 0 -> binding.textViewCount.setTextColor(Color.RED)
            else -> binding.textViewCount.setTextColor(Color.BLACK)
        }
    }
}
