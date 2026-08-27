package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.os.Build
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.data.model.Ayah
import com.example.data.model.PlaybackSpeed
import com.example.data.model.PlaybackState
import com.example.data.model.Reciter
import com.example.data.model.SleepTimerOption
import com.example.data.model.Surah
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class QuranAudioPlayer private constructor(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var sleepTimer: CountDownTimer? = null

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private val progressUpdater = object : Runnable {
        override fun run() {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    val currentPos = player.currentPosition.toLong()
                    val totalDur = player.duration.toLong()
                    val totalAyahs = _playbackState.value.totalAyahsInSurah.coerceAtLeast(1)
                    
                    // Estimate synchronized ayah highlight based on playback progression
                    val calculatedAyah = if (totalDur > 0) {
                        val progressFraction = currentPos.toFloat() / totalDur.toFloat()
                        (1 + (progressFraction * totalAyahs).toInt()).coerceIn(1, totalAyahs)
                    } else {
                        _playbackState.value.currentAyahNumber
                    }

                    _playbackState.value = _playbackState.value.copy(
                        currentPositionMs = currentPos,
                        durationMs = totalDur,
                        currentAyahNumber = calculatedAyah
                    )
                    handler.postDelayed(this, 500)
                }
            }
        }
    }

    fun playSurah(surah: Surah, reciter: Reciter = _playbackState.value.selectedReciter, startAyah: Int = 1) {
        stop()
        _playbackState.value = _playbackState.value.copy(
            isLoading = true,
            currentSurah = surah,
            currentAyahNumber = startAyah,
            totalAyahsInSurah = surah.totalVerses,
            selectedReciter = reciter
        )

        try {
            val audioUrl = String.format("%s%03d.mp3", reciter.serverUrl, surah.number)
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(audioUrl)
                setOnPreparedListener { mp ->
                    _playbackState.value = _playbackState.value.copy(
                        isLoading = false,
                        isPlaying = true,
                        durationMs = mp.duration.toLong()
                    )
                    applySpeed(_playbackState.value.speed)
                    mp.start()
                    handler.post(progressUpdater)
                }
                setOnCompletionListener {
                    onSurahCompleted()
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("QuranAudioPlayer", "MediaPlayer error: what=$what extra=$extra")
                    _playbackState.value = _playbackState.value.copy(isLoading = false, isPlaying = false)
                    false
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e("QuranAudioPlayer", "Failed to start audio", e)
            _playbackState.value = _playbackState.value.copy(isLoading = false, isPlaying = false)
        }
    }

    fun togglePlayPause() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
                _playbackState.value = _playbackState.value.copy(isPlaying = false)
                handler.removeCallbacks(progressUpdater)
            } else {
                player.start()
                _playbackState.value = _playbackState.value.copy(isPlaying = true)
                handler.post(progressUpdater)
            }
        }
    }

    fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _playbackState.value = _playbackState.value.copy(isPlaying = false)
                handler.removeCallbacks(progressUpdater)
            }
        }
    }

    fun resume() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
                _playbackState.value = _playbackState.value.copy(isPlaying = true)
                handler.post(progressUpdater)
            }
        }
    }

    fun seekTo(positionMs: Long) {
        mediaPlayer?.seekTo(positionMs.toInt())
        _playbackState.value = _playbackState.value.copy(currentPositionMs = positionMs)
    }

    fun seekForward15() {
        mediaPlayer?.let { player ->
            val newPos = (player.currentPosition + 15000).coerceAtMost(player.duration)
            player.seekTo(newPos)
            _playbackState.value = _playbackState.value.copy(currentPositionMs = newPos.toLong())
        }
    }

    fun seekRewind15() {
        mediaPlayer?.let { player ->
            val newPos = (player.currentPosition - 15000).coerceAtLeast(0)
            player.seekTo(newPos)
            _playbackState.value = _playbackState.value.copy(currentPositionMs = newPos.toLong())
        }
    }

    fun setPlaybackSpeed(speed: PlaybackSpeed) {
        _playbackState.value = _playbackState.value.copy(speed = speed)
        applySpeed(speed)
    }

    private fun applySpeed(speed: PlaybackSpeed) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mediaPlayer?.let { player ->
                try {
                    val params = player.playbackParams ?: PlaybackParams()
                    params.speed = speed.speed
                    player.playbackParams = params
                } catch (e: Exception) {
                    Log.e("QuranAudioPlayer", "Error applying speed", e)
                }
            }
        }
    }

    fun setSleepTimer(option: SleepTimerOption) {
        sleepTimer?.cancel()
        sleepTimer = null

        if (option == SleepTimerOption.OFF) {
            _playbackState.value = _playbackState.value.copy(
                sleepTimerOption = option,
                sleepTimerRemainingSeconds = 0L
            )
            return
        }

        if (option == SleepTimerOption.END_OF_SURAH) {
            _playbackState.value = _playbackState.value.copy(
                sleepTimerOption = option,
                sleepTimerRemainingSeconds = -1L
            )
            return
        }

        val durationMillis = option.minutes * 60 * 1000L
        _playbackState.value = _playbackState.value.copy(
            sleepTimerOption = option,
            sleepTimerRemainingSeconds = option.minutes * 60L
        )

        sleepTimer = object : CountDownTimer(durationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _playbackState.value = _playbackState.value.copy(
                    sleepTimerRemainingSeconds = millisUntilFinished / 1000
                )
            }

            override fun onFinish() {
                pause()
                _playbackState.value = _playbackState.value.copy(
                    sleepTimerOption = SleepTimerOption.OFF,
                    sleepTimerRemainingSeconds = 0L
                )
            }
        }.start()
    }

    fun seekToAyah(ayahNumber: Int) {
        val total = _playbackState.value.totalAyahsInSurah.coerceAtLeast(1)
        val targetAyah = ayahNumber.coerceIn(1, total)
        val duration = _playbackState.value.durationMs
        if (duration > 0) {
            val targetPositionMs = ((targetAyah.toFloat() / total.toFloat()) * duration).toLong()
            seekTo(targetPositionMs)
            _playbackState.value = _playbackState.value.copy(currentAyahNumber = targetAyah)
        }
    }

    private fun onSurahCompleted() {
        handler.removeCallbacks(progressUpdater)
        if (_playbackState.value.sleepTimerOption == SleepTimerOption.END_OF_SURAH) {
            _playbackState.value = _playbackState.value.copy(
                isPlaying = false,
                sleepTimerOption = SleepTimerOption.OFF,
                sleepTimerRemainingSeconds = 0L
            )
            return
        }

        // Auto play next surah if available
        val current = _playbackState.value.currentSurah
        if (current != null && current.number < 114) {
            // Next surah
            _playbackState.value = _playbackState.value.copy(
                isPlaying = false,
                currentPositionMs = 0L
            )
        } else {
            _playbackState.value = _playbackState.value.copy(isPlaying = false, currentPositionMs = 0L)
        }
    }

    fun stop() {
        handler.removeCallbacks(progressUpdater)
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            Log.e("QuranAudioPlayer", "Error stopping player", e)
        }
        mediaPlayer = null
        _playbackState.value = _playbackState.value.copy(isPlaying = false, isLoading = false, currentPositionMs = 0L)
    }

    companion object {
        @Volatile
        private var instance: QuranAudioPlayer? = null

        fun getInstance(context: Context): QuranAudioPlayer {
            return instance ?: synchronized(this) {
                instance ?: QuranAudioPlayer(context.applicationContext).also { instance = it }
            }
        }
    }
}
