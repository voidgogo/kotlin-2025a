package com.appweek12

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * CounterViewModelStateFlow
 *
 * LiveData와의 차이점:
 * - MutableStateFlow 사용 (MutableLiveData 대신)
 * - StateFlow로 공개 (LiveData 대신)
 * - 코루틴/Flow 기반
 *
 * 화면 회전해도 이 ViewModel은 살아있습니다!
 */
class CounterViewModel : ViewModel() {

    /**
     *  private val _count = MutableStateFlow(0)
     * - MutableStateFlow: 코루틴 기반의 mutable 상태
     * - 초기값: 0
     * - LiveData의 MutableLiveData와 유사하지만 Flow 기반
     */
    private val _count = MutableStateFlow(0)

    /**
     * public val count: StateFlow<Int> = _count.asStateFlow()
     * - StateFlow: 읽기 전용 상태 스트림
     * - asStateFlow(): _count를 읽기 전용으로 변환
     * - LiveData의 LiveData와 유사하지만 Flow 기반
     *
     * 왜 asStateFlow()를 사용?
     *    - _count = MutableStateFlow이므로 누구나 수정 가능
     *    - asStateFlow()로 감싸면 읽기 전용 인터페이스만 노출
     *    - Activity는 읽기만 가능 ✅
     */
    val count: StateFlow<Int> = _count.asStateFlow()

    /**
     * 증가 메소드
     */
    fun increment() {
        _count.value = (_count.value) + 1
    }

    /**
     * 감소 메소드
     */
    fun decrement() {
        _count.value = (_count.value) - 1
    }

    /**
     * 리셋 메소드
     */
    fun reset() {
        _count.value = 0
    }

    /**
     * 10씩 증가 메소드
     */
    fun incrementBy10() {
        _count.value = (_count.value) + 10
    }
}