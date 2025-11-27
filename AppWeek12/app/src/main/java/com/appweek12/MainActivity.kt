package com.appweek12

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.appweek12.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // savedInstanceState로 복원 시도 (번거로움)
        if (savedInstanceState != null) {
            count = savedInstanceState.getInt("count", 0)
        }

        setupListeners()
        updateCountDisplay()
    }

    /**
     * 화면 회전 시 데이터 저장 (복잡함)
     * 이것도 문제: 번들 크기에 제한 있음
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("count", count)
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

    /**
     * 수동으로 UI 업데이트 (매번 호출해야 함)
     */
    private fun updateCountDisplay() {
        binding.textViewCount.text = count.toString()

        when {
            count > 0 -> binding.textViewCount.setTextColor(Color.GREEN)
            count < 0 -> binding.textViewCount.setTextColor(Color.RED)
            else -> binding.textViewCount.setTextColor(Color.BLACK)
        }
    }
}
