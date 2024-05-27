package com.example.companytaskmanager.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InactivityHandler(private val timeoutMillis: Long) {

    private val _inactivityFlow = MutableStateFlow(false)
    val inactivityFlow: StateFlow<Boolean> = _inactivityFlow.asStateFlow()

    private var job: Job? = null

    fun resetTimeout() {
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            delay(timeoutMillis)
            _inactivityFlow.value = true
        }
    }

    fun stopTimeout() {
        job?.cancel()
    }

    fun resetInactivityFlag() {
        _inactivityFlow.value = false
    }
}