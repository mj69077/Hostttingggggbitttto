package com.example.audio

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat as MediaNotificationCompat
import com.example.MainActivity
import com.example.notifications.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class QuranPlaybackService : Service() {

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var audioPlayer: QuranAudioPlayer
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var isBound = false

    override fun onCreate() {
        super.onCreate()
        audioPlayer = QuranAudioPlayer.getInstance(this)
        NotificationHelper.createNotificationChannels(this)
        initMediaSession()
        observePlaybackState()
    }

    private fun initMediaSession() {
        mediaSession = MediaSessionCompat(this, "QuranPlaybackService").apply {
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    audioPlayer.resume()
                }

                override fun onPause() {
                    audioPlayer.pause()
                }

                override fun onFastForward() {
                    audioPlayer.seekForward15()
                }

                override fun onRewind() {
                    audioPlayer.seekRewind15()
                }

                override fun onStop() {
                    audioPlayer.stop()
                    stopForeground(true)
                    stopSelf()
                }

                override fun onSeekTo(pos: Long) {
                    audioPlayer.seekTo(pos)
                }
            })
            isActive = true
        }
    }

    private fun observePlaybackState() {
        serviceScope.launch {
            audioPlayer.playbackState.collectLatest { state ->
                val surah = state.currentSurah
                if (surah != null) {
                    updateMediaSessionState(state.isPlaying, state.currentPositionMs, state.durationMs)
                    val notification = buildNotification(
                        title = "سورة ${surah.nameArabic} - الآية ${state.currentAyahNumber}",
                        reciterName = state.selectedReciter.nameArabic,
                        isPlaying = state.isPlaying
                    )
                    startForeground(NOTIFICATION_ID, notification)
                } else {
                    stopForeground(true)
                }
            }
        }
    }

    private fun updateMediaSessionState(isPlaying: Boolean, position: Long, duration: Long) {
        val stateBuilder = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PAUSE or
                PlaybackStateCompat.ACTION_PLAY_PAUSE or
                PlaybackStateCompat.ACTION_REWIND or
                PlaybackStateCompat.ACTION_FAST_FORWARD or
                PlaybackStateCompat.ACTION_STOP or
                PlaybackStateCompat.ACTION_SEEK_TO
            )
            .setState(
                if (isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
                position,
                1.0f
            )

        mediaSession.setPlaybackState(stateBuilder.build())

        val currentSurah = audioPlayer.playbackState.value.currentSurah
        if (currentSurah != null) {
            val metadataBuilder = MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "سورة ${currentSurah.nameArabic}")
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, audioPlayer.playbackState.value.selectedReciter.nameArabic)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "القرآن الكريم")
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
            mediaSession.setMetadata(metadataBuilder.build())
        }
    }

    private fun buildNotification(title: String, reciterName: String, isPlaying: Boolean): android.app.Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "quran")
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        // Actions: Rewind 15s, Play/Pause, Forward 15s, Stop
        val rewindIntent = PendingIntent.getService(
            this, 1,
            Intent(this, QuranPlaybackService::class.java).apply { action = ACTION_REWIND_15 },
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val playPauseIntent = PendingIntent.getService(
            this, 2,
            Intent(this, QuranPlaybackService::class.java).apply { action = ACTION_PLAY_PAUSE },
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val forwardIntent = PendingIntent.getService(
            this, 3,
            Intent(this, QuranPlaybackService::class.java).apply { action = ACTION_FORWARD_15 },
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val stopIntent = PendingIntent.getService(
            this, 4,
            Intent(this, QuranPlaybackService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val playPauseIcon = if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
        val playPauseTitle = if (isPlaying) "إيقاف مؤقت" else "تشغيل"

        return NotificationCompat.Builder(this, NotificationHelper.CHANNEL_MEDIA_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle(title)
            .setContentText(reciterName)
            .setSubText("تلاوة خاشعة")
            .setContentIntent(openAppPendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(android.R.drawable.ic_media_rew, "تأخير ١٥ ث", rewindIntent)
            .addAction(playPauseIcon, playPauseTitle, playPauseIntent)
            .addAction(android.R.drawable.ic_media_ff, "تقديم ١٥ ث", forwardIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "إغلاق", stopIntent)
            .setStyle(
                MediaNotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
                    .setShowCancelButton(true)
                    .setCancelButtonIntent(stopIntent)
            )
            .setOngoing(isPlaying)
            .setOnlyAlertOnce(true)
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY_PAUSE -> audioPlayer.togglePlayPause()
            ACTION_REWIND_15 -> audioPlayer.seekRewind15()
            ACTION_FORWARD_15 -> audioPlayer.seekForward15()
            ACTION_STOP -> {
                audioPlayer.stop()
                stopForeground(true)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.release()
    }

    companion object {
        const val NOTIFICATION_ID = 501
        const val ACTION_PLAY_PAUSE = "com.example.audio.ACTION_PLAY_PAUSE"
        const val ACTION_REWIND_15 = "com.example.audio.ACTION_REWIND_15"
        const val ACTION_FORWARD_15 = "com.example.audio.ACTION_FORWARD_15"
        const val ACTION_STOP = "com.example.audio.ACTION_STOP"

        fun startService(context: Context) {
            val intent = Intent(context, QuranPlaybackService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, QuranPlaybackService::class.java)
            context.stopService(intent)
        }
    }
}
