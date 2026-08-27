package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class IndexCategory(val id: String, val titleArabic: String) {
    ALL("all", "الكل"),
    JUZ("juz", "الأجزاء"),
    HIZB("hizb", "الأحزاب"),
    THEMATIC("thematic", "الموضوعي"),
    SURAH("surah", "السور"),
    PAGE("page", "الصفحات")
}

@Entity(tableName = "quran_indexes")
data class QuranIndexItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "juz", "hizb", "thematic", "surah", "page"
    val itemNumber: Int,
    val titleArabic: String,
    val subtitleArabic: String = "",
    val startSurahNumber: Int = 1,
    val startSurahName: String = "",
    val startAyahNumber: Int = 1,
    val startPage: Int = 1,
    val endPage: Int = 1,
    val topicGroup: String = "", // e.g., "عقيدة", "عبادات", "قصص الأنبياء", "أخلاق", "يوم القيامة", "أدعية"
    val description: String = "",
    val keywords: String = ""
)
