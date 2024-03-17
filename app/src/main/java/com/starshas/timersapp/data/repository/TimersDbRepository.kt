package com.starshas.timersapp.data.repository

import com.starshas.timersapp.domain.models.TimerFinishTime

interface TimersDbRepository {
    suspend fun addTimer(finishTime: Long): Long
    suspend fun getAllTimers(): List<TimerFinishTime>
    suspend fun deleteById(id: Long)
}
