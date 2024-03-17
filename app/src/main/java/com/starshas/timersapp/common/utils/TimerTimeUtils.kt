package com.starshas.timersapp.common.utils

import com.starshas.timersapp.common.constants.TimeUtils.MILLIS_PER_SECOND
import com.starshas.timersapp.common.constants.TimeUtils.SECONDS_PER_HOUR
import com.starshas.timersapp.common.constants.TimeUtils.SECONDS_PER_MINUTE
import com.starshas.timersapp.common.extensions.intOrZero
import com.starshas.timersapp.common.models.TimerTime

object TimerTimeUtils {
    private fun fromTotalSeconds(id: Long, totalSeconds: Int): TimerTime {
        val hours = totalSeconds / SECONDS_PER_HOUR
        val minutes = (totalSeconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE
        val seconds = totalSeconds % SECONDS_PER_MINUTE
        return TimerTime(id, hours, minutes, seconds)
    }

    fun timerTimeFromTotalMillis(id: Long, totalMillis: Long): TimerTime {
        return fromTotalSeconds(id, (totalMillis / MILLIS_PER_SECOND).toInt())
    }

    fun TimerTime.decrementedOneSecond(): TimerTime {
        val totalSeconds = toTotalSeconds() - 1
        return fromTotalSeconds(id = id, totalSeconds.coerceAtLeast(0))
    }

    fun TimerTime.isFinished(): Boolean = toTotalSeconds() <= 0

    private fun TimerTime.toTotalSeconds(): Int =
        (hours ?: 0) * SECONDS_PER_HOUR +
                (minutes ?: 0) * SECONDS_PER_MINUTE +
                (seconds ?: 0)

    fun TimerTime.toTotalMillis(): Long = toTotalSeconds() * MILLIS_PER_SECOND.toLong()

    fun TimerTime.format() = "%02d:%02d:%02d".format(
        hours.intOrZero,
        minutes.intOrZero,
        seconds.intOrZero
    )
}
