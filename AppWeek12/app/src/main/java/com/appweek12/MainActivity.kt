package com.appweek12

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.appweek12.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
//    private var count = 0;

    private val viewModel: CounterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 아래 코드는 ViewModel이 자동으로 처리
//        if(savedInstanceState != null){
//            count = savedInstanceState.getInt("count", 0)
//        }

        setupObservers()
        setupListeners()
//        updateCountDisplay()
    }

    private fun setupObservers() {
        viewModel.count.observe(this){
            count -> binding.textViewCount.text = count.toString()

            when{
                count > 0 -> binding.textViewCount.setTextColor(Color.GREEN)
                count < 0 -> binding.textViewCount.setTextColor(Color.RED)
                else -> binding.textViewCount.setTextColor(Color.BLACK)
            }
        }
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putInt("count", count)
//    }

    private fun setupListeners() {
        binding.buttonPlus.setOnClickListener {
//            count++
//            updateCountDisplay()
            viewModel.increment()
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

//    private fun updateCountDisplay(){
//        binding.textViewCount.text = count.toString()
//
//        when{
//            count > 0 -> binding.textViewCount.setTextColor(Color.GREEN)
//            count < 0 -> binding.textViewCount.setTextColor(Color.RED)
//            else -> binding.textViewCount.setTextColor(Color.BLACK)
//        }
//    }
}