package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AthkarReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val isMorning = intent.getBooleanExtra("is_morning", true)
        NotificationHelper.showAthkarNotification(context, isMorning)
    }
}
