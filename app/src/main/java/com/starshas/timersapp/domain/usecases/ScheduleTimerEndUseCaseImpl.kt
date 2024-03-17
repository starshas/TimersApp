package com.starshas.timersapp.domain.usecases

import com.starshas.timersapp.data.AlarmScheduler
import com.starshas.timersapp.common.models.TimerTime
import com.starshas.timersapp.common.utils.TimerTimeUtils.toTotalMillis
import com.starshas.timersapp.common.models.AlarmTime
import javax.inject.Inject

class ScheduleTimerEndUseCaseImpl @Inject constructor(
    private val alarmScheduler: AlarmScheduler
) : ScheduleTimerEndUseCase {
    override operator fun invoke(timer: TimerTime) {
        alarmScheduler.scheduleTimerAlarm(
            AlarmTime(
                startTimeMillis = timer.toTotalMillis(),
                endTimeMillis = System.currentTimeMillis() + timer.toTotalMillis()
            )
        )
    }
}
