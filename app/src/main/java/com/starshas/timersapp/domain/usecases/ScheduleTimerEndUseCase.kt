package com.starshas.timersapp.domain.usecases

import com.starshas.timersapp.common.models.TimerTime

interface ScheduleTimerEndUseCase {
    operator fun invoke(timer: TimerTime)
}
