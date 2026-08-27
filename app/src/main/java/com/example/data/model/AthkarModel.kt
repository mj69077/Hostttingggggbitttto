package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AthkarCategory(
    val id: String,
    val titleArabic: String,
    val subtitleArabic: String
) {
    MORNING("morning", "أذكار الصباح", "حصن المسلم لبداية يوم مبارك"),
    EVENING("evening", "أذكار المساء", "السكينة والحفظ من كل سوء"),
    AFTER_PRAYER("after_prayer", "أذكار بعد الصلاة", "التسبيح والاستغفار دبر الصلوات"),
    SLEEP("sleep", "أذكار النوم", "راحة القلب وسكينة النفس"),
    WAKEUP("wakeup", "أذكار الاستيقاظ", "الحمد لله الذي أحيانا"),
    QURAN_DUAS("quran_duas", "أدعية قرآنية", "من جوامع دعاء القرآن الكريم");

    val icon: ImageVector
        get() = when (this) {
            MORNING -> Icons.Default.WbSunny
            EVENING -> Icons.Default.NightsStay
            AFTER_PRAYER -> Icons.Default.AccessTime
            SLEEP -> Icons.Default.Bedtime
            WAKEUP -> Icons.Default.WbSunny
            QURAN_DUAS -> Icons.Default.TouchApp
        }
}

@Entity(tableName = "athkar_items")
data class AthkarItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val categoryId: String,
    val textArabic: String,
    val countTarget: Int,
    val currentCount: Int = 0,
    val virtue: String = "",
    val reference: String = "",
    val isFavorite: Boolean = false,
    val isCompleted: Boolean = false
) {
    val virtueArabic: String get() = virtue
}

@Entity(tableName = "tasbih_records")
data class TasbihRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val phrase: String,
    val count: Int,
    val targetCount: Int = 33,
    val timestamp: Long = System.currentTimeMillis(),
    val dayKey: String = "" // format YYYY-MM-DD
)
