package com.example.localization

import java.text.NumberFormat
import java.util.Locale

object NumberFormatterHelper {
    fun formatNumber(value: Number, locale: Locale = Locale.getDefault()): String {
        return try {
            NumberFormat.getInstance(locale).format(value)
        } catch (e: Exception) {
            value.toString()
        }
    }

    fun formatDecimal(value: Double, fractionDigits: Int = 2, locale: Locale = Locale.getDefault()): String {
        return try {
            val nf = NumberFormat.getNumberInstance(locale)
            nf.minimumFractionDigits = fractionDigits
            nf.maximumFractionDigits = fractionDigits
            nf.format(value)
        } catch (e: Exception) {
            String.format(locale, "%.${fractionDigits}f", value)
        }
    }

    fun formatFileSize(bytes: Long, locale: Locale = Locale.getDefault()): String {
        if (bytes < 1024) return "${formatNumber(bytes, locale)} B"
        val kb = bytes / 1024.0
        if (kb < 1024) return "${formatDecimal(kb, 1, locale)} KB"
        val mb = kb / 1024.0
        if (mb < 1024) return "${formatDecimal(mb, 1, locale)} MB"
        val gb = mb / 1024.0
        return "${formatDecimal(gb, 2, locale)} GB"
    }

    fun formatDistance(meters: Double, isMetric: Boolean, locale: Locale = Locale.getDefault()): String {
        return if (isMetric) {
            if (meters < 1000) {
                "${formatDecimal(meters, 0, locale)} m"
            } else {
                val km = meters / 1000.0
                "${formatDecimal(km, 2, locale)} km"
            }
        } else {
            val feet = meters * 3.28084
            if (feet < 5280) {
                "${formatDecimal(feet, 0, locale)} ft"
            } else {
                val miles = feet / 5280.0
                "${formatDecimal(miles, 2, locale)} mi"
            }
        }
    }

    fun formatSpeed(metersPerSec: Double, isMetric: Boolean, locale: Locale = Locale.getDefault()): String {
        return if (isMetric) {
            val kmh = metersPerSec * 3.6
            "${formatDecimal(kmh, 1, locale)} km/h"
        } else {
            val mph = metersPerSec * 2.23694
            "${formatDecimal(mph, 1, locale)} mph"
        }
    }
}
