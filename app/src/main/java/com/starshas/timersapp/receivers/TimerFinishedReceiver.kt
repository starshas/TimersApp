package com.starshas.timersapp.receivers

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.starshas.timersapp.R

class TimerFinishedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        notifyUserTimeIsUp(context, intent)
        beep()
    }

    private fun notifyUserTimeIsUp(context: Context, intent: Intent?) {
        createNotificationChannel(context)
        val message = context.getString(
            R.string.notification_timer_finished,
            intent?.getStringExtra(STRING_EXTRA_TIMER) ?: ""
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.notification_time_s_up))
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    /* context = */ context,
                    /* permission = */ Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(NOTIFICATION_ID, notification)
        }
    }

    private fun createNotificationChannel(context: Context) {
        val name = CHANNEL_NAME
        val descriptionText = CHANNEL_DESCRIPTION
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun beep() {
        val toneGen = ToneGenerator(AudioManager.STREAM_ALARM, BEEP_VOLUME)
        toneGen.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, BEEP_DURATION_MILLIS)
        Handler(Looper.getMainLooper()).postDelayed({
            toneGen.release()
        }, BEEP_DURATION_MILLIS.toLong())
    }

    companion object {
        private const val BEEP_VOLUME: Int = 100
        private const val BEEP_DURATION_MILLIS: Int = 500
        const val STRING_EXTRA_TIMER: String = "STRING_EXTRA_TIMER"
        private const val CHANNEL_ID = "timer_finished_channel"
        private const val CHANNEL_NAME = "Timer channel"
        private const val CHANNEL_DESCRIPTION = "Timer channel"
        private const val NOTIFICATION_ID = 1
    }
}
