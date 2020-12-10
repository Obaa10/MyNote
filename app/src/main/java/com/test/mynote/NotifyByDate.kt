package com.test.mynote

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast


class NotifyByDate : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, "Alarm went off", Toast.LENGTH_SHORT).show()
    }
}
