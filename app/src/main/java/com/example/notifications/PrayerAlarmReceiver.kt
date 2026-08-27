package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PrayerAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra("prayer_name") ?: "الصلاة"
        val prayerTime = intent.getStringExtra("prayer_time") ?: ""
        val soundTitle = intent.getStringExtra("sound_title") ?: "أذان الحرم المكي"

        NotificationHelper.showPrayerNotification(context, prayerName, prayerTime, soundTitle)
    }
}
