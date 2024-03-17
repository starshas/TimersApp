package com.starshas.timersapp.data

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.starshas.timersapp.common.models.AlarmTime
import com.starshas.timersapp.common.utils.format
import com.starshas.timersapp.receivers.TimerFinishedReceiver
import kotlin.math.absoluteValue

class AlarmSchedulerImpl(private val context: Context) : AlarmScheduler {
    @SuppressLint("MissingPermission")
    override fun scheduleTimerAlarm(timerStartTime: AlarmTime) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent: PendingIntent = createPendingIntent(timerStartTime)
        val triggerAtMillis = timerStartTime.endTimeMillis
        val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerAtMillis, alarmIntent)

        try {
            alarmManager.setAlarmClock(alarmClockInfo, alarmIntent)
        } catch (e: SecurityException) {
            /* Not expected to happen while having android.permission.USE_EXACT_ALARM
               and targeting API level 33+ */
            Log.e("AlarmSchedulerImpl", "Error scheduling alarm", e)
        }
    }

    private fun createPendingIntent(timerStartTime: AlarmTime): PendingIntent {
        Intent(context, TimerFinishedReceiver::class.java).let { intent ->
            return PendingIntent.getBroadcast(
                context,
                timerStartTime.hashCode().absoluteValue,
                intent.apply {
                    putExtra(
                        TimerFinishedReceiver.STRING_EXTRA_TIMER, timerStartTime.format()
                    )
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}
