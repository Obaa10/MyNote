package com.test.mynote.notifyactivity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.test.mynote.R


class NotifyByDate() : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {

        val name = intent.getStringExtra("name")
        val date = intent.getIntegerArrayListExtra("date")!!
        val sDate = "${date[0]}/${date[1]}/${date[2]}"
        val builder = NotificationCompat.Builder(
            context!!,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.unnamed)
            .setContentTitle(name)
            .setContentText("You have a Date at $sDate")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = sDate
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
                notificationManager.notify(1001, builder.build())

        } else {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(1001, builder.build())
        }
    }

    companion object {
        const val CHANNEL_ID = "ss"
    }
}
