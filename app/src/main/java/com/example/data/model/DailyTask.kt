package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_tasks")
data class DailyTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // "fard", "sunnah", "dhikr", "charity", "quran"
    val categoryArabic: String,
    val rewardText: String = "",
    val isCompleted: Boolean = false,
    val dateKey: String = "" // YYYY-MM-DD
)
