package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fatwas")
data class Fatwa(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val question: String,
    val answer: String,
    val category: String, // "siyam", "salah", "zakat", "tahara", "general", "modern"
    val categoryArabic: String,
    val muftiOrSource: String = "اللجنة الدائمة / كبار العلماء",
    val isFavorite: Boolean = false,
    val keywords: String = ""
)
