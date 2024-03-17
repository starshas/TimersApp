package com.starshas.timersapp.common.utils

import com.starshas.timersapp.common.constants.TimeUtils.MILLIS_PER_SECOND
import com.starshas.timersapp.common.constants.TimeUtils.SECONDS_PER_HOUR
import com.starshas.timersapp.common.constants.TimeUtils.SECONDS_PER_MINUTE
import com.starshas.timersapp.common.models.AlarmTime

fun AlarmTime.format(): String {
    val totalSeconds = startTimeMillis / MILLIS_PER_SECOND
    val hours = totalSeconds / SECONDS_PER_HOUR
    val minutes = (totalSeconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE
    val seconds = totalSeconds % SECONDS_PER_MINUTE

    return "%02d:%02d:%02d".format(
        hours,
        minutes,
        seconds
    )
}
