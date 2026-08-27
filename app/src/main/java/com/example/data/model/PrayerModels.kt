package com.example.data.model

enum class PrayerType(val arabicName: String, val englishName: String) {
    FAJR("الفجر", "Fajr"),
    SUNRISE("الشروق", "Sunrise"),
    DHUHR("الظهر", "Dhuhr"),
    ASR("العصر", "Asr"),
    MAGHRIB("المغرب", "Maghrib"),
    ISHA("العشاء", "Isha")
}

data class PrayerTime(
    val type: PrayerType,
    val timeFormatted: String,
    val timeMillis: Long,
    val isNext: Boolean = false,
    val isPassed: Boolean = false,
    val isAdhanEnabled: Boolean = true
)

enum class AdhanSound(val id: String, val titleArabic: String, val resName: String) {
    MAKKAH("makkah", "أذان الحرم المكي", "adhan_makkah"),
    MADINAH("madinah", "أذان الحرم المدني", "adhan_madinah"),
    QUDS("quds", "أذان المسجد الأقصى", "adhan_quds"),
    EGYPT("egypt", "أذان مصر (الشيخ محمد رفعت)", "adhan_egypt"),
    TAKBEER_ONLY("takbeer", "تكبيرات فقط (تنبيه لطيف)", "takbeer")
}

enum class CalculationMethod(val id: String, val titleArabic: String, val fajrAngle: Double, val ishaAngle: Double) {
    UMM_AL_QURA("umm_al_qura", "أم القرى (مكة المكرمة)", 18.5, 19.0),
    MWL("mwl", "رابطة العالم الإسلامي", 18.0, 17.0),
    EGYPTIAN("egyptian", "الهيئة المصرية العامة للمساحة", 19.5, 17.5),
    KARACHI("karachi", "جامعة العلوم الإسلامية بكراتشي", 18.0, 18.0),
    ISNA("isna", "الجمعية الإسلامية لأمريكا الشمالية", 15.0, 15.0)
}

data class CityLocation(
    val nameArabic: String,
    val countryArabic: String,
    val latitude: Double,
    val longitude: Double,
    val timezoneOffsetHours: Double
)
