package com.appweek12

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CounterViewModel : ViewModel(){
    private val _count = MutableLiveData(0)
    val count: LiveData<Int> = _count

    fun increment(){
        _count.value = (_count.value ?: 0) + 1
    }
    fun decrement(){
        _count.value = (_count.value ?: 0) - 1
    }
    fun reset(){
        _count.value = 0
    }
    fun incrementBy10(){
        _count.value = (_count.value ?: 0) + 10
    }
}