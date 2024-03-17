package com.starshas.timersapp.domain.usecases

import com.starshas.timersapp.common.models.TimerTime
import com.starshas.timersapp.data.repository.TimersDbRepository
import com.starshas.timersapp.domain.mapper.timerFinishTimeToPresentation
import com.starshas.timersapp.domain.models.TimerFinishTime

class GetAllTimersUseCaseImpl(private val repository: TimersDbRepository) : GetAllTimersUseCase {
    override suspend operator fun invoke(currentTimeMillis: Long): List<TimerTime> =
        repository.getAllTimers().map { timerFinishTime: TimerFinishTime ->
            timerFinishTimeToPresentation(
                timerFinishTime.id,
                timerFinishTime.finishTimeMillis - currentTimeMillis
            )
        }
}
