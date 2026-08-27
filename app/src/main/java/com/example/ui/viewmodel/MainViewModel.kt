package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CalculationMethod
import com.example.data.model.CityLocation
import com.example.data.model.DailyReadingProgress
import com.example.data.model.KhatmahPlan
import com.example.data.model.PlaybackSpeed
import com.example.data.model.PlaybackState
import com.example.data.model.PrayerTime
import com.example.data.model.QuranSearchResult
import com.example.data.model.Reciter
import com.example.data.model.SleepTimerOption
import com.example.data.model.Surah
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository.getInstance(application)

    val allSurahs = repository.allSurahs.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val playbackState: StateFlow<PlaybackState> = repository.playbackState

    val activeKhatmahPlan: StateFlow<KhatmahPlan?> = repository.activeKhatmahPlan.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    val recentProgress: StateFlow<List<DailyReadingProgress>> = repository.recentDailyProgress.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val tasbihRecords = repository.allTasbihRecords.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allFatwas = repository.allFatwas.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun toggleFatwaFavorite(fatwa: com.example.data.model.Fatwa) {
        viewModelScope.launch {
            repository.toggleFatwaFavorite(fatwa)
        }
    }

    // --- Quran Indexes ---
    val allQuranIndexes = repository.allQuranIndexes.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun getQuranIndexesByType(type: String) = repository.getQuranIndexesByType(type)
    fun getQuranIndexesByTopic(topicGroup: String) = repository.getQuranIndexesByTopic(topicGroup)

    suspend fun searchQuranIndexes(query: String): List<com.example.data.model.QuranIndexItem> {
        return repository.searchQuranIndexes(query)
    }

    private val _prayerTimes = MutableStateFlow<List<PrayerTime>>(emptyList())
    val prayerTimes: StateFlow<List<PrayerTime>> = _prayerTimes.asStateFlow()

    private val _selectedCity = MutableStateFlow(repository.getSelectedCity())
    val selectedCity: StateFlow<CityLocation> = _selectedCity.asStateFlow()

    private val _qiblaAngle = MutableStateFlow(repository.getQiblaAngle())
    val qiblaAngle: StateFlow<Double> = _qiblaAngle.asStateFlow()

    private val _searchResults = MutableStateFlow<List<QuranSearchResult>>(emptyList())
    val searchResults: StateFlow<List<QuranSearchResult>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _lastReadSurah = MutableStateFlow<Surah?>(null)
    val lastReadSurah: StateFlow<Surah?> = _lastReadSurah.asStateFlow()

    private val _lastReadAyah = MutableStateFlow(1)
    val lastReadAyah: StateFlow<Int> = _lastReadAyah.asStateFlow()

    private val _hapticEnabled = MutableStateFlow(repository.isHapticEnabled())
    val hapticEnabled: StateFlow<Boolean> = _hapticEnabled.asStateFlow()

    init {
        refreshPrayerTimes()
        loadLastRead()
    }

    fun refreshPrayerTimes() {
        _prayerTimes.value = repository.getPrayerTimes()
        _qiblaAngle.value = repository.getQiblaAngle()
    }

    private fun loadLastRead() {
        viewModelScope.launch {
            val surahNum = repository.getLastReadSurah()
            _lastReadAyah.value = repository.getLastReadAyah()
            _lastReadSurah.value = repository.getSurah(surahNum)
        }
    }

    // Search
    fun search(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            _isSearching.value = false
            return
        }
        viewModelScope.launch {
            _isSearching.value = true
            _searchResults.value = repository.searchQuran(query)
            _isSearching.value = false
        }
    }

    fun clearSearch() {
        _searchResults.value = emptyList()
        _isSearching.value = false
    }

    // Audio Controls
    fun playSurah(surah: Surah, reciter: Reciter? = null, startAyah: Int = 1) {
        repository.playSurah(surah, reciter, startAyah)
        saveLastRead(surah.number, startAyah)
    }

    fun togglePlayPause() = repository.togglePlayPause()
    fun seekTo(positionMs: Long) = repository.seekTo(positionMs)
    fun seekForward15() = repository.seekForward15()
    fun seekRewind15() = repository.seekRewind15()
    fun seekToAyah(ayahNumber: Int) = repository.seekToAyah(ayahNumber)
    fun setPlaybackSpeed(speed: PlaybackSpeed) = repository.setPlaybackSpeed(speed)
    fun setSleepTimer(option: SleepTimerOption) = repository.setSleepTimer(option)
    fun stopAudio() = repository.stopAudio()

    // Quran State
    fun getVersesForSurah(surahNumber: Int) = repository.getVersesForSurah(surahNumber)

    fun saveLastRead(surahNumber: Int, ayahNumber: Int) {
        repository.saveLastRead(surahNumber, ayahNumber)
        loadLastRead()
    }

    // Khatmah
    fun updateKhatmahPage(newPage: Int) {
        viewModelScope.launch {
            repository.updateKhatmahPage(newPage)
        }
    }

    fun saveKhatmahPlan(targetDays: Int, startPage: Int = 1) {
        viewModelScope.launch {
            repository.saveKhatmahPlan(targetDays, startPage)
        }
    }

    // Athkar & Tasbih
    fun getAthkarByCategory(categoryId: String) = repository.getAthkarByCategory(categoryId)

    fun incrementAthkar(item: com.example.data.model.AthkarItem) {
        viewModelScope.launch {
            repository.updateAthkarCount(item)
        }
    }

    fun resetAthkarCategory(categoryId: String) {
        viewModelScope.launch {
            repository.resetAthkarCategory(categoryId)
        }
    }

    fun recordTasbih(phrase: String, count: Int, target: Int = 33) {
        viewModelScope.launch {
            repository.recordTasbih(phrase, count, target)
        }
    }

    fun triggerTapHaptic() = repository.performTapHaptic()
    fun triggerGoalHaptic() = repository.performGoalHaptic()

    // Settings
    fun setSelectedCity(city: CityLocation) {
        repository.setSelectedCity(city)
        _selectedCity.value = city
        refreshPrayerTimes()
    }

    fun setCalculationMethod(method: CalculationMethod) {
        repository.setCalculationMethod(method)
        refreshPrayerTimes()
    }

    fun getCalculationMethod() = repository.getCalculationMethod()
    fun getAdhanSound() = repository.getAdhanSound()
    fun setAdhanSound(sound: com.example.data.model.AdhanSound) = repository.setAdhanSound(sound)

    fun setHapticEnabled(enabled: Boolean) {
        repository.setHapticEnabled(enabled)
        _hapticEnabled.value = enabled
    }

    // Backup & Restore
    suspend fun exportBackupJson(): String = repository.exportUserDataToJson()

    suspend fun importBackupJson(json: String): Boolean {
        val success = repository.importUserDataFromJson(json)
        if (success) {
            loadLastRead()
            refreshPrayerTimes()
            _hapticEnabled.value = repository.isHapticEnabled()
        }
        return success
    }
}
