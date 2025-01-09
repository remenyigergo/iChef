package com.example.ichef.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationManager
import android.app.PendingIntent
import android.health.connect.datatypes.units.Power
import android.os.PowerManager
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import com.example.ichef.activities.MainActivity
import com.example.ichef.adapters.SharedData
import com.example.ichef.database.ShoppingDataManager
import com.example.ichef.notifications.channels.ShoppingListReminderNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DailyShoppingListNotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var storeDatabase: ShoppingDataManager

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("DailyShoppingListNotificationReceiver", "Notification triggered")

        // Get the shopping list from the database
        val shoppingList = storeDatabase.getStores()

        // Wake the device
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "app:notificationWakeLock"
        )
        wakeLock.acquire(3000) // Acquire the wake lock for 3 seconds

        try {
            if (shoppingList.isNotEmpty()) {
                val notificationIntent = Intent(context, MainActivity::class.java).apply {
                    putExtra("fragment_to_open", "shoppingFragment")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }

                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val notification = ShoppingListReminderNotificationManager.createNotification(
                    context,
                    "Have you been shopping?",
                    "Don't forget to check your shopping list!"
                )
                    .setContentIntent(pendingIntent)
                    .build()

                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.notify(1, notification)

                Log.d("DailyShoppingListNotificationReceiver", "Notification sent successfully")
            } else {
                Log.d("DailyShoppingListNotificationReceiver", "No notification sent: Shopping list is empty.")
            }
        } catch (e: Exception) {
            Log.e("DailyShoppingListNotificationReceiver", "Error while sending notification", e)
        } finally {
            // Ensure wakeLock is released no matter what happens
            if (wakeLock.isHeld) {
                wakeLock.release()
            }
        }
    }
}
