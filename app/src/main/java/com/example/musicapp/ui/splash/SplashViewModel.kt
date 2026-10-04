package com.example.musicapp.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class SplashViewModel : ViewModel() {
    private val _loading = MutableStateFlow<Boolean>(false)
    val loading = _loading.asStateFlow()
 fun delay() {
        viewModelScope.launch {
            delay(3000)
            _loading.emit(true)
        }
    }

}