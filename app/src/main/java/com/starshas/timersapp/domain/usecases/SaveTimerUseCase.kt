package com.starshas.timersapp.domain.usecases

import com.starshas.timersapp.common.models.TimerTime

interface SaveTimerUseCase {
    suspend operator fun invoke(
        timerTime: TimerTime,
        currentTimeMillis: Long
    )
}
