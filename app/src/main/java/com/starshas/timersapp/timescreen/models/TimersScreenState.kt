package com.starshas.timersapp.presentation.feature.timescreen.models

import com.starshas.timersapp.common.models.TimerTime

data class TimersScreenState(
    val time: TimerTime = TimerTime(),
    val list: List<TimerTime> = emptyList()
)
