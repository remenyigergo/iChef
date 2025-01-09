package com.example.ichef.notifications.channels
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.ichef.R

object ShoppingListReminderNotificationManager {
    private const val CHANNEL_ID = "daily_notification_channel"
    private const val CHANNEL_NAME = "Daily Notification"

    fun createNotificationChannel(context: Context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notification for daily reminder to update shopping list accordingly."
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    fun createNotification(context: Context, title: String, message: String): NotificationCompat.Builder {

        val largeIcon = BitmapFactory.decodeResource(context.resources, R.mipmap.shopping_fruits_foreground)
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon_full_suit)
            .setLargeIcon(largeIcon)
            .setColor(ContextCompat.getColor(context, R.color.selectedButton))
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX) // High priority for pre-Oreo devices
            .setDefaults(NotificationCompat.DEFAULT_ALL)  // Sound, vibration, and lights
            .setAutoCancel(true)
    }
}
