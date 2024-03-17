package com.starshas.timersapp.domain.usecases

import com.starshas.timersapp.common.models.TimerTime

interface GetAllTimersUseCase {
    suspend operator fun invoke(currentTimeMillis: Long): List<TimerTime>
}
