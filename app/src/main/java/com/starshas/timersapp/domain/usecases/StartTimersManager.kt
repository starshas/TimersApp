package com.starshas.timersapp.domain.usecases

import com.starshas.timersapp.common.models.TimerTime
import kotlinx.coroutines.flow.StateFlow

interface StartTimersManager {
    fun addTimer(initialTime: TimerTime)
    fun removeAllTimers()
    suspend fun startTimers()
    fun getTimersFlow(): StateFlow<List<TimerTime>>
}
