package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "khatmah_plans")
data class KhatmahPlan(
    @PrimaryKey(autoGenerate = true) val id: Int = 1,
    val title: String = "ختمة القرآن الكريم",
    val targetDays: Int = 30,
    val startPage: Int = 1,
    val currentPage: Int = 1,
    val totalPages: Int = 604,
    val startDateMillis: Long = System.currentTimeMillis(),
    val targetDailyPages: Int = 20,
    val isActive: Boolean = true
) {
    val progressFraction: Float
        get() = (currentPage.toFloat() / totalPages.toFloat()).coerceIn(0f, 1f)
}

@Entity(tableName = "daily_progress")
data class DailyReadingProgress(
    @PrimaryKey val dateKey: String, // YYYY-MM-DD
    val pagesRead: Int = 0,
    val athkarCompleted: Int = 0,
    val tasbihCount: Int = 0,
    val prayersOnTime: Int = 0
)

data class AppBackupData(
    val exportDate: String,
    val appVersion: String = "1.0.0",
    val lastReadSurah: Int = 1,
    val lastReadAyah: Int = 1,
    val khatmahPlan: KhatmahPlan? = null,
    val dailyProgressList: List<DailyReadingProgress> = emptyList(),
    val tasbihRecords: List<TasbihRecord> = emptyList(),
    val calculationMethod: String = "umm_al_qura",
    val selectedCity: String = "مكة المكرمة",
    val adhanSound: String = "makkah",
    val hapticEnabled: Boolean = true,
    val morningAthkarAlertTime: String = "05:00",
    val eveningAthkarAlertTime: String = "17:00"
)
