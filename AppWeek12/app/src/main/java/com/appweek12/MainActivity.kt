package com.appweek12

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.appweek12.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    /**
     * StateFlow 버전도 by viewModels() 사용
     * (LiveData와 동일하게 화면 회전 시 데이터 유지)
     */
    private val viewModel: CounterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupListeners()
    }

    /**
     * StateFlow 관찰 설정
     *
     * LiveData vs StateFlow 수집 방법 비교:
     *
     * LiveData (observe):
     *   viewModel.count.observe(this) { count ->
     *       binding.textViewCount.text = count.toString()
     *   }
     *
     * StateFlow (collect):
     *   lifecycleScope.launch {
     *       repeatOnLifecycle(Lifecycle.State.STARTED) {
     *           viewModel.count.collect { count ->
     *               binding.textViewCount.text = count.toString()
     *           }
     *       }
     *   }
     */
    private fun setupObservers() {
        /**
         * lifecycleScope.launch
         * - Activity의 lifecycle과 연동된 Coroutine Scope
         * - Activity 파괴 시 자동으로 cancel됨
         */
        lifecycleScope.launch {
            /**
             * repeatOnLifecycle(Lifecycle.State.STARTED)
             * - STARTED 상태(화면에 보임)에서만 collect
             * - STOPPED 상태(화면에 안 보임)에서는 자동 취소
             * - LiveData의 observe()와 동일한 효과
             *
             * 생명주기 상태:
             *    CREATED → STARTED → RESUMED → PAUSED → STOPPED → DESTROYED
             */
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                /**
                 * viewModel.count.collect { count ->
                 * - StateFlow를 수집
                 * - count가 변할 때마다 실행
                 * - collect는 suspend 함수 (람다도 suspend 함수)
                 */
                viewModel.count.collect { count ->
                    binding.textViewCount.text = count.toString()

                    when {
                        count > 0 -> binding.textViewCount.setTextColor(Color.GREEN)
                        count < 0 -> binding.textViewCount.setTextColor(Color.RED)
                        else -> binding.textViewCount.setTextColor(Color.BLACK)
                    }
                }
            }
        }
    }

    /**
     * 버튼 리스너
     * StateFlow 버전도 ViewModel 메소드만 호출
     * UI 업데이트는 collect에서 처리 (LiveData와 동일)
     */
    private fun setupListeners() {
        binding.buttonPlus.setOnClickListener {
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
}