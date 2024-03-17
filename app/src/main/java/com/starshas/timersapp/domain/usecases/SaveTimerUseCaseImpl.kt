package com.starshas.timersapp.domain.usecases

import com.starshas.timersapp.common.models.TimerTime
import com.starshas.timersapp.common.utils.TimerTimeUtils.toTotalMillis
import com.starshas.timersapp.data.repository.TimersDbRepository

class SaveTimerUseCaseImpl(private val repository: TimersDbRepository) : SaveTimerUseCase {
    override suspend operator fun invoke(
        timerTime: TimerTime,
        currentTimeMillis: Long
    ) {
        repository.addTimer(currentTimeMillis + timerTime.toTotalMillis())
    }
}
