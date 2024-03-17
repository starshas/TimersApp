package com.starshas.timersapp.data

import com.starshas.timersapp.common.models.AlarmTime

interface AlarmScheduler {
    fun scheduleTimerAlarm(timerStartTime: AlarmTime)
}
