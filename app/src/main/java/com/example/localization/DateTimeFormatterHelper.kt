package com.example.localization

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateTimeFormatterHelper {
    val COMMON_TIMEZONES: List<String> = listOf(
        "UTC",
        "Asia/Kolkata",
        "Asia/Dhaka",
        "America/New_York",
        "America/Los_Angeles",
        "America/Chicago",
        "Europe/London",
        "Europe/Berlin",
        "Europe/Paris",
        "Asia/Dubai",
        "Asia/Tokyo",
        "Asia/Seoul",
        "Asia/Shanghai",
        "Australia/Sydney",
        "America/Sao_Paulo",
        "Africa/Johannesburg"
    )

    fun formatTime(
        epochMillis: Long,
        timeZoneId: String = "UTC",
        is24Hour: Boolean = true,
        locale: Locale = Locale.getDefault()
    ): String {
        return try {
            val zone = ZoneId.of(timeZoneId)
            val instant = Instant.ofEpochMilli(epochMillis)
            val pattern = if (is24Hour) "HH:mm:ss" else "hh:mm:ss a"
            val formatter = DateTimeFormatter.ofPattern(pattern, locale).withZone(zone)
            formatter.format(instant)
        } catch (e: Exception) {
            // Fallback
            val instant = Instant.ofEpochMilli(epochMillis)
            DateTimeFormatter.ISO_INSTANT.format(instant)
        }
    }

    fun formatDate(
        epochMillis: Long,
        timeZoneId: String = "UTC",
        locale: Locale = Locale.getDefault()
    ): String {
        return try {
            val zone = ZoneId.of(timeZoneId)
            val instant = Instant.ofEpochMilli(epochMillis)
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", locale).withZone(zone)
            formatter.format(instant)
        } catch (e: Exception) {
            "2026-09-16"
        }
    }

    fun formatDateTime(
        epochMillis: Long,
        timeZoneId: String = "UTC",
        is24Hour: Boolean = true,
        locale: Locale = Locale.getDefault()
    ): String {
        return "${formatDate(epochMillis, timeZoneId, locale)} ${formatTime(epochMillis, timeZoneId, is24Hour, locale)}"
    }
}
