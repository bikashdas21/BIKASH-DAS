package com.example.localization

import java.util.Locale

data class CountryInfo(
    val code: String,
    val name: String,
    val nativeName: String,
    val flagEmoji: String,
    val callingCode: String,
    val defaultTimeZone: String,
    val defaultEmergencyNumber: String,
    val defaultLanguage: String,
    val loraFrequencyBand: String,
    val isMetricDefault: Boolean = true
)

object CountryRegistry {
    val COUNTRIES: List<CountryInfo> = listOf(
        CountryInfo(
            code = "GLOBAL",
            name = "Worldwide / International",
            nativeName = "Global Mesh",
            flagEmoji = "🌍",
            callingCode = "+0",
            defaultTimeZone = "UTC",
            defaultEmergencyNumber = "112",
            defaultLanguage = "en",
            loraFrequencyBand = "ISM 2.4 GHz / Regional",
            isMetricDefault = true
        ),
        CountryInfo(
            code = "US",
            name = "United States",
            nativeName = "United States",
            flagEmoji = "🇺🇸",
            callingCode = "+1",
            defaultTimeZone = "America/New_York",
            defaultEmergencyNumber = "911",
            defaultLanguage = "en",
            loraFrequencyBand = "US915 (902-928 MHz)",
            isMetricDefault = false
        ),
        CountryInfo(
            code = "IN",
            name = "India",
            nativeName = "भारत",
            flagEmoji = "🇮🇳",
            callingCode = "+91",
            defaultTimeZone = "Asia/Kolkata",
            defaultEmergencyNumber = "112",
            defaultLanguage = "hi",
            loraFrequencyBand = "IN865 (865-867 MHz)",
            isMetricDefault = true
        ),
        CountryInfo(
            code = "BD",
            name = "Bangladesh",
            nativeName = "বাংলাদেশ",
            flagEmoji = "🇧🇩",
            callingCode = "+880",
            defaultTimeZone = "Asia/Dhaka",
            defaultEmergencyNumber = "999",
            defaultLanguage = "bn",
            loraFrequencyBand = "AS923 (923 MHz)",
            isMetricDefault = true
        ),
        CountryInfo(
            code = "GB",
            name = "United Kingdom",
            nativeName = "United Kingdom",
            flagEmoji = "🇬🇧",
            callingCode = "+44",
            defaultTimeZone = "Europe/London",
            defaultEmergencyNumber = "999",
            defaultLanguage = "en",
            loraFrequencyBand = "EU868 (863-870 MHz)",
            isMetricDefault = true
        ),
        CountryInfo(
            code = "DE",
            name = "Germany",
            nativeName = "Deutschland",
            flagEmoji = "🇩🇪",
            callingCode = "+49",
            defaultTimeZone = "Europe/Berlin",
            defaultEmergencyNumber = "112",
            defaultLanguage = "de",
            loraFrequencyBand = "EU868 (863-870 MHz)",
            isMetricDefault = true
        ),
        CountryInfo(
            code = "FR",
            name = "France",
            nativeName = "France",
            flagEmoji = "🇫🇷",
            callingCode = "+33",
            defaultTimeZone = "Europe/Paris",
            defaultEmergencyNumber = "112",
            defaultLanguage = "fr",
            loraFrequencyBand = "EU868 (863-870 MHz)",
            isMetricDefault = true
        ),
        CountryInfo(
            code = "ES",
            name = "Spain",
            nativeName = "España",
            flagEmoji = "🇪🇸",
            callingCode = "+34",
            defaultTimeZone = "Europe/Madrid",
            defaultEmergencyNumber = "112",
            defaultLanguage = "es",
            loraFrequencyBand = "EU868 (863-870 MHz)",
            isMetricDefault = true
        ),
        CountryInfo(
            code = "BR",
            name = "Brazil",
            nativeName = "Brasil",
            flagEmoji = "🇧🇷",
            callingCode = "+55",
            defaultTimeZone = "America/Sao_Paulo",
            defaultEmergencyNumber = "190",
            defaultLanguage = "pt",
            loraFrequencyBand = "AU915 (915-928 MHz)",
            isMetricDefault = true
        ),
        CountryInfo(
            code = "AE",
            name = "United Arab Emirates",
            nativeName = "الإمارات العربية المتحدة",
            flagEmoji = "🇦🇪",
            callingCode = "+971",
            defaultTimeZone = "Asia/Dubai",
            defaultEmergencyNumber = "999",
            defaultLanguage = "ar",
            loraFrequencyBand = "AS923 / EU868",
            isMetricDefault = true
        ),
        CountryInfo(
            code = "CN",
            name = "China",
            nativeName = "中国",
            flagEmoji = "🇨🇳",
            callingCode = "+86",
            defaultTimeZone = "Asia/Shanghai",
            defaultEmergencyNumber = "110",
            defaultLanguage = "zh",
            loraFrequencyBand = "CN470 (470-510 MHz)",
            isMetricDefault = true
        ),
        CountryInfo(
            code = "JP",
            name = "Japan",
            nativeName = "日本",
            flagEmoji = "🇯🇵",
            callingCode = "+81",
            defaultTimeZone = "Asia/Tokyo",
            defaultEmergencyNumber = "110",
            defaultLanguage = "ja",
            loraFrequencyBand = "AS923-1 (920-928 MHz)",
            isMetricDefault = true
        ),
        CountryInfo(
            code = "KR",
            name = "South Korea",
            nativeName = "대한민국",
            flagEmoji = "🇰🇷",
            callingCode = "+82",
            defaultTimeZone = "Asia/Seoul",
            defaultEmergencyNumber = "112",
            defaultLanguage = "ko",
            loraFrequencyBand = "KR920 (920-923 MHz)",
            isMetricDefault = true
        ),
        CountryInfo(
            code = "AU",
            name = "Australia",
            nativeName = "Australia",
            flagEmoji = "🇦🇺",
            callingCode = "+61",
            defaultTimeZone = "Australia/Sydney",
            defaultEmergencyNumber = "000",
            defaultLanguage = "en",
            loraFrequencyBand = "AU915 (915-928 MHz)",
            isMetricDefault = true
        ),
        CountryInfo(
            code = "CA",
            name = "Canada",
            nativeName = "Canada",
            flagEmoji = "🇨🇦",
            callingCode = "+1",
            defaultTimeZone = "America/Toronto",
            defaultEmergencyNumber = "911",
            defaultLanguage = "en",
            loraFrequencyBand = "US915 (902-928 MHz)",
            isMetricDefault = true
        ),
        CountryInfo(
            code = "ZA",
            name = "South Africa",
            nativeName = "South Africa",
            flagEmoji = "🇿🇦",
            callingCode = "+27",
            defaultTimeZone = "Africa/Johannesburg",
            defaultEmergencyNumber = "112",
            defaultLanguage = "en",
            loraFrequencyBand = "EU868 (868 MHz)",
            isMetricDefault = true
        ),
        CountryInfo(
            code = "MX",
            name = "Mexico",
            nativeName = "México",
            flagEmoji = "🇲🇽",
            callingCode = "+52",
            defaultTimeZone = "America/Mexico_City",
            defaultEmergencyNumber = "911",
            defaultLanguage = "es",
            loraFrequencyBand = "US915 (902-928 MHz)",
            isMetricDefault = true
        ),
        CountryInfo(
            code = "RU",
            name = "Russia",
            nativeName = "Россия",
            flagEmoji = "🇷🇺",
            callingCode = "+7",
            defaultTimeZone = "Europe/Moscow",
            defaultEmergencyNumber = "112",
            defaultLanguage = "ru",
            loraFrequencyBand = "RU864 (864-870 MHz)",
            isMetricDefault = true
        )
    )

    fun getCountryByCode(code: String): CountryInfo {
        return COUNTRIES.find { it.code.equals(code, ignoreCase = true) } ?: COUNTRIES.first()
    }

    fun detectSystemCountry(): CountryInfo {
        val sysCountry = Locale.getDefault().country
        return COUNTRIES.find { it.code.equals(sysCountry, ignoreCase = true) }
            ?: COUNTRIES.find { it.code == "GLOBAL" }
            ?: COUNTRIES.first()
    }
}
