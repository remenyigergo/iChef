package com.example.ichef.notifications.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.ichef.notifications.DailyShoppingListNotificationReceiver
import java.util.Calendar

object AlarmScheduler {

    private const val EIGHT_HOURS_IN_MILLIS = 8 * 60 * 60 * 1000L


    fun scheduleDailyNotification(context: Context, hour: Int, minute: Int, second: Int) { //TODO this can be used later on. At the moment cron scheduled
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DailyShoppingListNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, second)
        }

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            EIGHT_HOURS_IN_MILLIS,
            pendingIntent
        )
    }
}
