package com.example.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity

object NotificationHelper {

    const val CHANNEL_PRAYER_ID = "channel_prayer_times"
    const val CHANNEL_ATHKAR_ID = "channel_athkar_reminders"
    const val CHANNEL_MEDIA_ID = "channel_quran_playback"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Prayer Channel (High Importance with sound)
            val prayerChannel = NotificationChannel(
                CHANNEL_PRAYER_ID,
                "مواقيت الصلاة والأذان",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "تنبيهات دخول وقت الصلاة والأذان اليومي"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
            }

            // Athkar Channel
            val athkarChannel = NotificationChannel(
                CHANNEL_ATHKAR_ID,
                "أذكار الصباح والمساء",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "تنبيهات ورد أذكار الصباح وأذكار المساء"
                enableVibration(true)
            }

            // Media Channel (Low/Default for sticky media playback)
            val mediaChannel = NotificationChannel(
                CHANNEL_MEDIA_ID,
                "تلاوة القرآن الكريم",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "التحكم في تشغيل تلاوة القرآن الكريم وخلفية الصوت"
                setShowBadge(false)
            }

            notificationManager.createNotificationChannels(listOf(prayerChannel, athkarChannel, mediaChannel))
        }
    }

    fun showPrayerNotification(context: Context, prayerName: String, timeFormatted: String, soundTitle: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "prayer")
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            101,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_PRAYER_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("حان الآن موعد أذان $prayerName")
            .setContentText("الله أكبر، حان وقت صلاة $prayerName ($timeFormatted)")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

        try {
            NotificationManagerCompat.from(context).notify(201, builder.build())
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun showAthkarNotification(context: Context, isMorning: Boolean) {
        val title = if (isMorning) "أذكار الصباح المباركة" else "أذكار المساء والسكينة"
        val desc = if (isMorning) "ابدأ يومك بذكر الله وحصن نفسك بأذكار الصباح" else "اختم يومك بالسكينة واستفتح ليلك بأذكار المساء"

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "athkar")
            putExtra("athkar_type", if (isMorning) "morning" else "evening")
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            102,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ATHKAR_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(desc)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        try {
            NotificationManagerCompat.from(context).notify(if (isMorning) 301 else 302, builder.build())
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}
