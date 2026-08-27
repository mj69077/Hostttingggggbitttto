package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "surahs")
data class Surah(
    @PrimaryKey val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val nameTransliteration: String,
    val revelationType: String, // Meccan or Medinan
    val totalVerses: Int,
    val startPage: Int = 1,
    val audioUrl: String = ""
) {
    val revelationTypeArabic: String
        get() = if (revelationType.equals("Meccan", ignoreCase = true) || revelationType.contains("مكي")) "مكية" else "مدنية"

    val pageNumber: Int
        get() = startPage
}

@Entity(tableName = "verses")
data class Ayah(
    @PrimaryKey val id: Int, // e.g., surahNumber * 1000 + ayahNumber
    val surahNumber: Int,
    val ayahNumber: Int,
    val textUthmani: String,
    val textPlain: String,
    val translationArabic: String = "",
    val tafseerMuyassar: String = "",
    val tafseerJalalayn: String = "",
    val pageNumber: Int = 1,
    val juzNumber: Int = 1,
    val sajda: Boolean = false,
    val audioUrl: String = ""
) {
    val verseNumber: Int get() = ayahNumber
    val textArabic: String get() = textUthmani.ifBlank { textPlain }
    val tafsirArabic: String get() = tafseerMuyassar.ifBlank { tafseerJalalayn.ifBlank { translationArabic } }
    val translationEnglish: String get() = translationArabic
}

data class Reciter(
    val id: String,
    val nameArabic: String,
    val nameEnglish: String,
    val subfolder: String, // URL format identifier
    val serverUrl: String
)

val RECITERS_LIST = listOf(
    Reciter(
        id = "alafasy",
        nameArabic = "مشاري راشد العفاسي",
        nameEnglish = "Mishari Rashid Alafasy",
        subfolder = "Alafasy_64kbps",
        serverUrl = "https://server8.mp3quran.net/afs/"
    ),
    Reciter(
        id = "abdul_basit",
        nameArabic = "عبد الباسط عبد الصمد (مرتل)",
        nameEnglish = "Abdul Basit Abdul Samad",
        subfolder = "Abdul_Basit_Murattal_64kbps",
        serverUrl = "https://server7.mp3quran.net/basit/"
    ),
    Reciter(
        id = "sudais",
        nameArabic = "عبد الرحمن السديس",
        nameEnglish = "Abdur-Rahman as-Sudais",
        subfolder = "Abdurrahmaan_As-Sudais_64kbps",
        serverUrl = "https://server11.mp3quran.net/sds/"
    ),
    Reciter(
        id = "shuraym",
        nameArabic = "سعود الشريم",
        nameEnglish = "Saud ash-Shuraym",
        subfolder = "Saood_ash-Shuraym_64kbps",
        serverUrl = "https://server7.mp3quran.net/shur/"
    ),
    Reciter(
        id = "ghamadi",
        nameArabic = "سعد الغامدي",
        nameEnglish = "Saad Al-Ghamdi",
        subfolder = "Ghamadi_40kbps",
        serverUrl = "https://server7.mp3quran.net/s_gmd/"
    ),
    Reciter(
        id = "husary",
        nameArabic = "محمود خليل الحصري",
        nameEnglish = "Mahmoud Khalil Al-Husary",
        subfolder = "Husary_64kbps",
        serverUrl = "https://server13.mp3quran.net/husr/"
    ),
    Reciter(
        id = "minshawi",
        nameArabic = "محمد صديق المنشاوي (مرتل)",
        nameEnglish = "Mohamed Siddiq Al-Minshawi",
        subfolder = "Minshawy_Murattal_128kbps",
        serverUrl = "https://server10.mp3quran.net/minsh/"
    ),
    Reciter(
        id = "ajamy",
        nameArabic = "أحمد بن علي العجمي",
        nameEnglish = "Ahmed ibn Ali al-Ajamy",
        subfolder = "Ahmed_ibn_Ali_al-Ajamy_64kbps",
        serverUrl = "https://server10.mp3quran.net/ajm/"
    )
)

data class QuranSearchResult(
    val ayah: Ayah,
    val surahName: String
)

enum class SleepTimerOption(val titleArabic: String, val minutes: Int) {
    OFF("معطل", 0),
    MINUTES_5("5 دقائق", 5),
    MINUTES_15("15 دقيقة", 15),
    MINUTES_30("30 دقيقة", 30),
    MINUTES_45("45 دقيقة", 45),
    MINUTES_60("ساعة كاملة", 60),
    END_OF_SURAH("نهاية السورة الحالية", -1)
}

enum class PlaybackSpeed(val speed: Float, val label: String) {
    SPEED_0_75(0.75f, "0.75x"),
    SPEED_1_0(1.0f, "1.0x (طبيعي)"),
    SPEED_1_25(1.25f, "1.25x"),
    SPEED_1_5(1.5f, "1.5x")
}

data class PlaybackState(
    val isPlaying: Boolean = false,
    val currentSurah: Surah? = null,
    val currentAyahNumber: Int = 1,
    val totalAyahsInSurah: Int = 1,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val speed: PlaybackSpeed = PlaybackSpeed.SPEED_1_0,
    val sleepTimerOption: SleepTimerOption = SleepTimerOption.OFF,
    val sleepTimerRemainingSeconds: Long = 0L,
    val selectedReciter: Reciter = RECITERS_LIST[0],
    val isLoading: Boolean = false
)
