package com.example.data.repository

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.example.audio.QuranAudioPlayer
import com.example.audio.QuranPlaybackService
import com.example.data.local.AppDatabase
import com.example.data.local.OfflineAthkarData
import com.example.data.local.OfflineQuranData
import com.example.data.local.OfflineQuranIndexData
import com.example.data.model.AdhanSound
import com.example.data.model.AppBackupData
import com.example.data.model.AthkarItem
import com.example.data.model.Ayah
import com.example.data.model.CalculationMethod
import com.example.data.model.CityLocation
import com.example.data.model.DailyReadingProgress
import com.example.data.model.KhatmahPlan
import com.example.data.model.PlaybackSpeed
import com.example.data.model.PrayerTime
import com.example.data.model.QuranIndexItem
import com.example.data.model.QuranSearchResult
import com.example.data.model.Reciter
import com.example.data.model.SleepTimerOption
import com.example.data.model.Surah
import com.example.data.model.TasbihRecord
import com.example.data.network.PrayerCalculationEngine
import com.example.notifications.AthkarReminderReceiver
import com.example.notifications.PrayerAlarmReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AppRepository private constructor(private val context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val quranDao = db.quranDao()
    private val quranIndexDao = db.quranIndexDao()
    private val athkarDao = db.athkarDao()
    private val tasbihDao = db.tasbihDao()
    private val khatmahDao = db.khatmahDao()
    private val fatwaDao = db.fatwaDao()
    private val taskDao = db.taskDao()
    private val audioPlayer = QuranAudioPlayer.getInstance(context)

    private val prefs: SharedPreferences = context.getSharedPreferences("quran_app_prefs", Context.MODE_PRIVATE)
    private val ioScope = CoroutineScope(Dispatchers.IO)

    init {
        ioScope.launch {
            seedDatabaseIfEmpty()
            scheduleDailyAlarms()
        }
    }

    private suspend fun seedDatabaseIfEmpty() {
        if (quranDao.getSurahsCount() == 0) {
            quranDao.insertSurahs(OfflineQuranData.getInitialSurahs())
        }
        if (quranDao.getVersesCount() == 0) {
            quranDao.insertVerses(OfflineQuranData.getInitialVerses())
        }
        if (quranIndexDao.getCount() == 0) {
            quranIndexDao.insertAll(OfflineQuranIndexData.getInitialIndexes())
        }
        if (athkarDao.getAthkarCount() == 0) {
            athkarDao.insertAthkar(OfflineAthkarData.getInitialAthkar())
        }
        if (fatwaDao.getCount() == 0) {
            fatwaDao.insertAll(com.example.data.local.OfflineFatwasData.fatwas)
        }
        if (khatmahDao.getActivePlanSync() == null) {
            khatmahDao.insertOrUpdatePlan(KhatmahPlan())
        }
    }

    // --- Quran Indexes ---
    val allQuranIndexes: Flow<List<QuranIndexItem>> = quranIndexDao.getAllIndices()

    fun getQuranIndexesByType(type: String): Flow<List<QuranIndexItem>> = quranIndexDao.getIndicesByType(type)

    fun getQuranIndexesByTopic(topicGroup: String): Flow<List<QuranIndexItem>> = quranIndexDao.getIndicesByTopic("thematic", topicGroup)

    suspend fun searchQuranIndexes(query: String): List<QuranIndexItem> = quranIndexDao.searchIndices(query)

    // --- Fatwas ---
    val allFatwas: Flow<List<com.example.data.model.Fatwa>> = fatwaDao.getAllFatwas()

    suspend fun toggleFatwaFavorite(fatwa: com.example.data.model.Fatwa) {
        fatwaDao.update(fatwa.copy(isFavorite = !fatwa.isFavorite))
    }

    // --- Quran & Verses ---
    val allSurahs: Flow<List<Surah>> = quranDao.getAllSurahs()

    fun getVersesForSurah(surahNumber: Int): Flow<List<Ayah>> = quranDao.getVersesForSurah(surahNumber)

    suspend fun getSurah(number: Int): Surah? = quranDao.getSurahByNumber(number)

    suspend fun getAyah(surahNumber: Int, ayahNumber: Int): Ayah? = quranDao.getAyah(surahNumber, ayahNumber)

    suspend fun searchQuran(rawQuery: String): List<QuranSearchResult> {
        val cleanQuery = rawQuery.trim()
        if (cleanQuery.isEmpty()) return emptyList()

        val normalized = normalizeArabic(cleanQuery)
        val directResults = quranDao.searchVerses(normalized)
        val surahs = quranDao.getAllSurahs().first().associateBy { it.number }

        return directResults.map { ayah ->
            QuranSearchResult(
                ayah = ayah,
                surahName = surahs[ayah.surahNumber]?.nameArabic ?: "سورة ${ayah.surahNumber}"
            )
        }
    }

    private fun normalizeArabic(text: String): String {
        return text
            .replace("[ًٌٍَُِّْـ]".toRegex(), "")
            .replace("[أإآٱ]".toRegex(), "ا")
            .replace("ة", "ه")
            .replace("ى", "ي")
    }

    // --- Audio Playback ---
    val playbackState = audioPlayer.playbackState

    fun playSurah(surah: Surah, reciter: Reciter? = null, startAyah: Int = 1) {
        val selected = reciter ?: audioPlayer.playbackState.value.selectedReciter
        audioPlayer.playSurah(surah, selected, startAyah)
        QuranPlaybackService.startService(context)
        saveLastRead(surah.number, startAyah)
    }

    fun togglePlayPause() = audioPlayer.togglePlayPause()
    fun pauseAudio() = audioPlayer.pause()
    fun resumeAudio() = audioPlayer.resume()
    fun seekTo(positionMs: Long) = audioPlayer.seekTo(positionMs)
    fun seekForward15() = audioPlayer.seekForward15()
    fun seekRewind15() = audioPlayer.seekRewind15()
    fun seekToAyah(ayahNumber: Int) = audioPlayer.seekToAyah(ayahNumber)
    fun setPlaybackSpeed(speed: PlaybackSpeed) = audioPlayer.setPlaybackSpeed(speed)
    fun setSleepTimer(option: SleepTimerOption) = audioPlayer.setSleepTimer(option)
    fun stopAudio() {
        audioPlayer.stop()
        QuranPlaybackService.stopService(context)
    }

    // --- Athkar & Tasbih ---
    fun getAthkarByCategory(categoryId: String): Flow<List<AthkarItem>> = athkarDao.getAthkarByCategory(categoryId)

    suspend fun updateAthkarCount(item: AthkarItem) {
        val nextCount = item.currentCount + 1
        val completed = nextCount >= item.countTarget
        athkarDao.updateProgress(item.id, nextCount, completed)

        if (completed) {
            performGoalHaptic()
            logDailyAthkarIncrement()
        } else {
            performTapHaptic()
        }
    }

    suspend fun resetAthkarCategory(categoryId: String) {
        athkarDao.resetCategory(categoryId)
    }

    val allTasbihRecords: Flow<List<TasbihRecord>> = tasbihDao.getAllTasbihRecords()

    suspend fun recordTasbih(phrase: String, count: Int, target: Int = 33) {
        val dayKey = getTodayDateKey()
        tasbihDao.insertTasbihRecord(
            TasbihRecord(
                phrase = phrase,
                count = count,
                targetCount = target,
                timestamp = System.currentTimeMillis(),
                dayKey = dayKey
            )
        )
        // Update daily progress
        val currentProgress = khatmahDao.getDailyProgressForDate(dayKey) ?: DailyReadingProgress(dayKey)
        khatmahDao.insertOrUpdateDailyProgress(
            currentProgress.copy(tasbihCount = currentProgress.tasbihCount + count)
        )
    }

    // --- Haptic Feedback ---
    fun performTapHaptic() {
        if (!isHapticEnabled()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                )
            } else {
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(30)
                }
            }
        } catch (e: Exception) {
            Log.e("AppRepository", "Haptic error", e)
        }
    }

    fun performGoalHaptic() {
        if (!isHapticEnabled()) return
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)?.defaultVibrator
            } else {
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 100, 80, 100, 80, 200)
                val amplitudes = intArrayOf(0, 200, 0, 220, 0, 255)
                vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 100, 80, 100, 80, 200), -1)
            }
        } catch (e: Exception) {
            Log.e("AppRepository", "Goal haptic error", e)
        }
    }

    // --- Khatmah & Daily Progress ---
    val activeKhatmahPlan: Flow<KhatmahPlan?> = khatmahDao.getActivePlan()
    val recentDailyProgress: Flow<List<DailyReadingProgress>> = khatmahDao.getRecentDailyProgress()

    suspend fun updateKhatmahPage(newPage: Int) {
        val plan = khatmahDao.getActivePlanSync() ?: KhatmahPlan()
        val updated = plan.copy(currentPage = newPage.coerceIn(1, plan.totalPages))
        khatmahDao.insertOrUpdatePlan(updated)

        val dayKey = getTodayDateKey()
        val currentProgress = khatmahDao.getDailyProgressForDate(dayKey) ?: DailyReadingProgress(dayKey)
        khatmahDao.insertOrUpdateDailyProgress(
            currentProgress.copy(pagesRead = currentProgress.pagesRead + 1)
        )
    }

    suspend fun saveKhatmahPlan(targetDays: Int, startPage: Int = 1) {
        val dailyPages = (604 - startPage + 1) / targetDays.coerceAtLeast(1)
        val plan = KhatmahPlan(
            targetDays = targetDays,
            startPage = startPage,
            currentPage = startPage,
            startDateMillis = System.currentTimeMillis(),
            targetDailyPages = dailyPages.coerceAtLeast(1),
            isActive = true
        )
        khatmahDao.insertOrUpdatePlan(plan)
    }

    private suspend fun logDailyAthkarIncrement() {
        val dayKey = getTodayDateKey()
        val current = khatmahDao.getDailyProgressForDate(dayKey) ?: DailyReadingProgress(dayKey)
        khatmahDao.insertOrUpdateDailyProgress(
            current.copy(athkarCompleted = current.athkarCompleted + 1)
        )
    }

    // --- Prayer Times & Alerts ---
    fun getPrayerTimes(): List<PrayerTime> {
        val city = getSelectedCity()
        val method = getCalculationMethod()
        return PrayerCalculationEngine.calculatePrayerTimes(city.latitude, city.longitude, method)
    }

    fun getQiblaAngle(): Double {
        val city = getSelectedCity()
        return PrayerCalculationEngine.calculateQiblaDirection(city.latitude, city.longitude)
    }

    fun scheduleDailyAlarms() {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val prayers = getPrayerTimes()
            val sound = getAdhanSound()

            // Schedule Azan alarms for upcoming prayers
            prayers.filter { !it.isPassed && it.isAdhanEnabled }.forEachIndexed { index, prayer ->
                val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                    putExtra("prayer_name", prayer.type.arabicName)
                    putExtra("prayer_time", prayer.timeFormatted)
                    putExtra("sound_title", sound.titleArabic)
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    1000 + index,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, prayer.timeMillis, pendingIntent)
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, prayer.timeMillis, pendingIntent)
                }
            }

            // Morning Athkar Alarm (e.g. 5:30 AM)
            val morningCal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 5)
                set(Calendar.MINUTE, 30)
                set(Calendar.SECOND, 0)
                if (timeInMillis < System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
            }
            val morningIntent = Intent(context, AthkarReminderReceiver::class.java).apply {
                putExtra("is_morning", true)
            }
            val morningPending = PendingIntent.getBroadcast(
                context, 2001, morningIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
            )
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, morningCal.timeInMillis, AlarmManager.INTERVAL_DAY, morningPending)

            // Evening Athkar Alarm (e.g. 5:00 PM)
            val eveningCal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 17)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                if (timeInMillis < System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
            }
            val eveningIntent = Intent(context, AthkarReminderReceiver::class.java).apply {
                putExtra("is_morning", false)
            }
            val eveningPending = PendingIntent.getBroadcast(
                context, 2002, eveningIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
            )
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, eveningCal.timeInMillis, AlarmManager.INTERVAL_DAY, eveningPending)

        } catch (e: Exception) {
            Log.e("AppRepository", "Failed to schedule alarms", e)
        }
    }

    // --- User Preferences ---
    fun getSelectedCity(): CityLocation {
        val cityName = prefs.getString("selected_city", "مكة المكرمة") ?: "مكة المكرمة"
        return PrayerCalculationEngine.DEFAULT_CITIES.firstOrNull { it.nameArabic == cityName }
            ?: PrayerCalculationEngine.DEFAULT_CITIES[0]
    }

    fun setSelectedCity(city: CityLocation) {
        prefs.edit().putString("selected_city", city.nameArabic).apply()
        scheduleDailyAlarms()
    }

    fun getCalculationMethod(): CalculationMethod {
        val methodId = prefs.getString("calc_method", CalculationMethod.UMM_AL_QURA.id)
        return CalculationMethod.values().firstOrNull { it.id == methodId } ?: CalculationMethod.UMM_AL_QURA
    }

    fun setCalculationMethod(method: CalculationMethod) {
        prefs.edit().putString("calc_method", method.id).apply()
        scheduleDailyAlarms()
    }

    fun getAdhanSound(): AdhanSound {
        val id = prefs.getString("adhan_sound", AdhanSound.MAKKAH.id)
        return AdhanSound.values().firstOrNull { it.id == id } ?: AdhanSound.MAKKAH
    }

    fun setAdhanSound(sound: AdhanSound) {
        prefs.edit().putString("adhan_sound", sound.id).apply()
    }

    fun isHapticEnabled(): Boolean = prefs.getBoolean("haptic_enabled", true)
    fun setHapticEnabled(enabled: Boolean) = prefs.edit().putBoolean("haptic_enabled", enabled).apply()

    fun saveLastRead(surahNumber: Int, ayahNumber: Int) {
        prefs.edit()
            .putInt("last_read_surah", surahNumber)
            .putInt("last_read_ayah", ayahNumber)
            .apply()
    }

    fun getLastReadSurah(): Int = prefs.getInt("last_read_surah", 1)
    fun getLastReadAyah(): Int = prefs.getInt("last_read_ayah", 1)

    // --- JSON Backup and Restore ---
    suspend fun exportUserDataToJson(): String {
        val khatmah = khatmahDao.getActivePlanSync()
        val dailyProgress = khatmahDao.getAllDailyProgressList()
        val tasbihList = tasbihDao.getTasbihRecordsList()

        val root = JSONObject().apply {
            put("exportDate", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
            put("appVersion", "1.0.0")
            put("lastReadSurah", getLastReadSurah())
            put("lastReadAyah", getLastReadAyah())
            put("selectedCity", getSelectedCity().nameArabic)
            put("calculationMethod", getCalculationMethod().id)
            put("adhanSound", getAdhanSound().id)
            put("hapticEnabled", isHapticEnabled())

            if (khatmah != null) {
                put("khatmahPlan", JSONObject().apply {
                    put("title", khatmah.title)
                    put("targetDays", khatmah.targetDays)
                    put("startPage", khatmah.startPage)
                    put("currentPage", khatmah.currentPage)
                    put("totalPages", khatmah.totalPages)
                    put("targetDailyPages", khatmah.targetDailyPages)
                    put("startDateMillis", khatmah.startDateMillis)
                })
            }

            val progressArray = JSONArray()
            dailyProgress.forEach { dp ->
                progressArray.put(JSONObject().apply {
                    put("dateKey", dp.dateKey)
                    put("pagesRead", dp.pagesRead)
                    put("athkarCompleted", dp.athkarCompleted)
                    put("tasbihCount", dp.tasbihCount)
                    put("prayersOnTime", dp.prayersOnTime)
                })
            }
            put("dailyProgress", progressArray)

            val tasbihArray = JSONArray()
            tasbihList.forEach { tr ->
                tasbihArray.put(JSONObject().apply {
                    put("phrase", tr.phrase)
                    put("count", tr.count)
                    put("targetCount", tr.targetCount)
                    put("timestamp", tr.timestamp)
                    put("dayKey", tr.dayKey)
                })
            }
            put("tasbihRecords", tasbihArray)
        }

        return root.toString(2)
    }

    suspend fun importUserDataFromJson(jsonString: String): Boolean {
        return try {
            val root = JSONObject(jsonString)

            if (root.has("lastReadSurah")) {
                saveLastRead(root.getInt("lastReadSurah"), root.optInt("lastReadAyah", 1))
            }
            if (root.has("selectedCity")) {
                val cityName = root.getString("selectedCity")
                PrayerCalculationEngine.DEFAULT_CITIES.firstOrNull { it.nameArabic == cityName }?.let {
                    setSelectedCity(it)
                }
            }
            if (root.has("calculationMethod")) {
                val methodId = root.getString("calculationMethod")
                CalculationMethod.values().firstOrNull { it.id == methodId }?.let {
                    setCalculationMethod(it)
                }
            }
            if (root.has("adhanSound")) {
                val soundId = root.getString("adhanSound")
                AdhanSound.values().firstOrNull { it.id == soundId }?.let {
                    setAdhanSound(it)
                }
            }
            if (root.has("hapticEnabled")) {
                setHapticEnabled(root.getBoolean("hapticEnabled"))
            }

            if (root.has("khatmahPlan")) {
                val kp = root.getJSONObject("khatmahPlan")
                val plan = KhatmahPlan(
                    title = kp.optString("title", "ختمة القرآن الكريم"),
                    targetDays = kp.optInt("targetDays", 30),
                    startPage = kp.optInt("startPage", 1),
                    currentPage = kp.optInt("currentPage", 1),
                    totalPages = kp.optInt("totalPages", 604),
                    targetDailyPages = kp.optInt("targetDailyPages", 20),
                    startDateMillis = kp.optLong("startDateMillis", System.currentTimeMillis()),
                    isActive = true
                )
                khatmahDao.insertOrUpdatePlan(plan)
            }

            if (root.has("dailyProgress")) {
                val arr = root.getJSONArray("dailyProgress")
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    khatmahDao.insertOrUpdateDailyProgress(
                        DailyReadingProgress(
                            dateKey = obj.getString("dateKey"),
                            pagesRead = obj.optInt("pagesRead", 0),
                            athkarCompleted = obj.optInt("athkarCompleted", 0),
                            tasbihCount = obj.optInt("tasbihCount", 0),
                            prayersOnTime = obj.optInt("prayersOnTime", 0)
                        )
                    )
                }
            }

            if (root.has("tasbihRecords")) {
                val arr = root.getJSONArray("tasbihRecords")
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    tasbihDao.insertTasbihRecord(
                        TasbihRecord(
                            phrase = obj.getString("phrase"),
                            count = obj.getInt("count"),
                            targetCount = obj.optInt("targetCount", 33),
                            timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                            dayKey = obj.optString("dayKey", getTodayDateKey())
                        )
                    )
                }
            }

            true
        } catch (e: Exception) {
            Log.e("AppRepository", "Import failed", e)
            false
        }
    }

    private fun getTodayDateKey(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    companion object {
        @Volatile
        private var INSTANCE: AppRepository? = null

        fun getInstance(context: Context): AppRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
