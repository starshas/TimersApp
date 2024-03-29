package com.starshas.timersapp.domain.mapper

import com.starshas.timersapp.common.models.TimerTime
import com.starshas.timersapp.common.utils.TimerTimeUtils

fun timerFinishTimeToPresentation(id: Long, time: Long): TimerTime {
    return TimerTimeUtils.timerTimeFromTotalMillis(id, time)
}
