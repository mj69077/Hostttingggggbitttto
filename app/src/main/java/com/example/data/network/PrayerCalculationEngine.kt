package com.example.data.network

import com.example.data.model.CalculationMethod
import com.example.data.model.CityLocation
import com.example.data.model.PrayerTime
import com.example.data.model.PrayerType
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.*

object PrayerCalculationEngine {

    val DEFAULT_CITIES = listOf(
        CityLocation("مكة المكرمة", "السعودية", 21.4225, 39.8262, 3.0),
        CityLocation("المدينة المنورة", "السعودية", 24.4672, 39.6111, 3.0),
        CityLocation("القدس الشريف", "فلسطين", 31.7683, 35.2137, 2.0),
        CityLocation("القاهرة", "مصر", 30.0444, 31.2357, 2.0),
        CityLocation("الرياض", "السعودية", 24.7136, 46.6753, 3.0),
        CityLocation("دبي", "الإمارات", 25.2048, 55.2708, 4.0),
        CityLocation("عمّان", "الأردن", 31.9454, 35.9284, 3.0),
        CityLocation("بيروت", "لبنان", 33.8938, 35.5018, 2.0),
        CityLocation("دمشق", "سوريا", 33.5138, 36.2765, 3.0),
        CityLocation("بغداد", "العراق", 33.3152, 44.3661, 3.0),
        CityLocation("الكويت", "الكويت", 29.3759, 47.9774, 3.0),
        CityLocation("الدوحة", "قطر", 25.2854, 51.5310, 3.0),
        CityLocation("المنامة", "البحرين", 26.2285, 50.5860, 3.0),
        CityLocation("مسقط", "عُمان", 23.5880, 58.3829, 4.0),
        CityLocation("تونس", "تونس", 36.8065, 10.1815, 1.0),
        CityLocation("الجزائر", "الجزائر", 36.7538, 3.0588, 1.0),
        CityLocation("الرباط", "المغرب", 34.0209, -6.8416, 1.0),
        CityLocation("طرابلس", "ليبيا", 32.8872, 13.1913, 2.0),
        CityLocation("الخرطوم", "السودان", 15.5007, 32.5599, 2.0),
        CityLocation("إسطنبول", "تركيا", 41.0082, 28.9784, 3.0),
        CityLocation("لندن", "المملكة المتحدة", 51.5074, -0.1278, 0.0)
    )

    fun calculatePrayerTimes(
        latitude: Double,
        longitude: Double,
        method: CalculationMethod = CalculationMethod.UMM_AL_QURA,
        calendar: Calendar = Calendar.getInstance()
    ): List<PrayerTime> {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val timezoneOffset = calendar.timeZone.rawOffset / (1000.0 * 60 * 60)

        val julianDay = getJulianDay(year, month, day) - (longitude / (15.0 * 24.0))
        val d = julianDay - 2451545.0

        val q = 280.459 + 0.98564736 * d
        val g = fixAngle(357.529 + 0.98560028 * d)
        val l = fixAngle(q + 1.915 * sin(Math.toRadians(g)) + 0.020 * sin(Math.toRadians(2 * g)))

        val e = 23.439 - 0.00000036 * d
        val ra = Math.toDegrees(atan2(cos(Math.toRadians(e)) * sin(Math.toRadians(l)), cos(Math.toRadians(l)))) / 15.0
        val fixedRa = fixHour(ra)

        val declination = Math.toDegrees(asin(sin(Math.toRadians(e)) * sin(Math.toRadians(l))))
        val eqOfTime = q / 15.0 - fixedRa

        val noon = fixHour(12.0 + timezoneOffset - (longitude / 15.0) - eqOfTime)

        // Sun angles for prayer
        val fajrTime = computeTime(noon, -method.fajrAngle, latitude, declination, isMorning = true)
        val sunriseTime = computeTime(noon, -0.8333, latitude, declination, isMorning = true)
        
        // Asr (Shafi'i = 1 shadow, Hanafi = 2 shadow)
        val asrAngle = -Math.toDegrees(atan(1.0 + tan(Math.toRadians(abs(latitude - declination)))))
        val asrTime = computeTime(noon, asrAngle, latitude, declination, isMorning = false)

        val sunsetTime = computeTime(noon, -0.8333, latitude, declination, isMorning = false)
        val ishaTime = if (method == CalculationMethod.UMM_AL_QURA) {
            sunsetTime + 1.5 // 90 minutes after Maghrib
        } else {
            computeTime(noon, -method.ishaAngle, latitude, declination, isMorning = false)
        }

        val nowMs = calendar.timeInMillis
        val baseCal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        fun toMillis(hours: Double): Long {
            val h = hours.toInt()
            val m = ((hours - h) * 60).toInt()
            val cal = baseCal.clone() as Calendar
            cal.set(Calendar.HOUR_OF_DAY, h % 24)
            cal.set(Calendar.MINUTE, m % 60)
            return cal.timeInMillis
        }

        fun formatHours(hours: Double): String {
            val h = (hours.toInt() % 24)
            val m = (((hours - hours.toInt()) * 60).toInt()) % 60
            val ampm = if (h < 12) "ص" else "م"
            val displayH = if (h == 0) 12 else if (h > 12) h - 12 else h
            return String.format("%02d:%02d %s", displayH, m, ampm)
        }

        val fajrMillis = toMillis(fajrTime)
        val sunriseMillis = toMillis(sunriseTime)
        val dhuhrMillis = toMillis(noon)
        val asrMillis = toMillis(asrTime)
        val maghribMillis = toMillis(sunsetTime)
        val ishaMillis = toMillis(ishaTime)

        val prayers = listOf(
            PrayerTime(PrayerType.FAJR, formatHours(fajrTime), fajrMillis),
            PrayerTime(PrayerType.SUNRISE, formatHours(sunriseTime), sunriseMillis),
            PrayerTime(PrayerType.DHUHR, formatHours(noon), dhuhrMillis),
            PrayerTime(PrayerType.ASR, formatHours(asrTime), asrMillis),
            PrayerTime(PrayerType.MAGHRIB, formatHours(sunsetTime), maghribMillis),
            PrayerTime(PrayerType.ISHA, formatHours(ishaTime), ishaMillis)
        )

        var foundNext = false
        return prayers.map { p ->
            val passed = nowMs > p.timeMillis
            val isNext = if (!passed && !foundNext) {
                foundNext = true
                true
            } else false

            p.copy(isNext = isNext, isPassed = passed)
        }
    }

    fun calculateQiblaDirection(latitude: Double, longitude: Double): Double {
        val kaabaLat = Math.toRadians(21.4225)
        val kaabaLng = Math.toRadians(39.8262)

        val userLat = Math.toRadians(latitude)
        val userLng = Math.toRadians(longitude)

        val dLng = kaabaLng - userLng
        val y = sin(dLng) * cos(kaabaLat)
        val x = cos(userLat) * sin(kaabaLat) - sin(userLat) * cos(kaabaLat) * cos(dLng)

        var qibla = Math.toDegrees(atan2(y, x))
        qibla = (qibla + 360) % 360
        return qibla
    }

    private fun computeTime(noon: Double, angle: Double, lat: Double, dec: Double, isMorning: Boolean): Double {
        val radLat = Math.toRadians(lat)
        val radDec = Math.toRadians(dec)
        val radAngle = Math.toRadians(angle)

        val cosH = (sin(radAngle) - sin(radLat) * sin(radDec)) / (cos(radLat) * cos(radDec))
        val clampedCosH = cosH.coerceIn(-1.0, 1.0)
        val h = Math.toDegrees(acos(clampedCosH)) / 15.0
        return if (isMorning) noon - h else noon + h
    }

    private fun getJulianDay(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    private fun fixAngle(angle: Double): Double {
        var a = angle - 360.0 * floor(angle / 360.0)
        if (a < 0) a += 360.0
        return a
    }

    private fun fixHour(hour: Double): Double {
        var h = hour - 24.0 * floor(hour / 24.0)
        if (h < 0) h += 24.0
        return h
    }
}
