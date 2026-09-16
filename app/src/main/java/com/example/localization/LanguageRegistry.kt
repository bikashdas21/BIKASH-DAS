package com.example.localization

data class LanguageInfo(
    val code: String,
    val nativeName: String,
    val englishName: String,
    val flag: String,
    val isRtl: Boolean = false
)

object LanguageRegistry {
    val LANGUAGES: List<LanguageInfo> = listOf(
        LanguageInfo("bn", "বাংলা", "Bengali", "🇧🇩", false),
        LanguageInfo("en", "English", "English", "🌐", false),
        LanguageInfo("hi", "हिन्दी", "Hindi", "🇮🇳", false),
        LanguageInfo("es", "Español", "Spanish", "🇪🇸", false),
        LanguageInfo("fr", "Français", "French", "🇫🇷", false),
        LanguageInfo("de", "Deutsch", "German", "🇩🇪", false),
        LanguageInfo("pt", "Português", "Portuguese", "🇧🇷", false),
        LanguageInfo("ar", "العربية", "Arabic", "🇦🇪", true),
        LanguageInfo("zh", "中文", "Chinese", "🇨🇳", false),
        LanguageInfo("ja", "日本語", "Japanese", "🇯🇵", false),
        LanguageInfo("ko", "한국어", "Korean", "🇰🇷", false),
        LanguageInfo("ru", "Русский", "Russian", "🇷🇺", false)
    )

    fun getLanguage(code: String): LanguageInfo {
        return LANGUAGES.find { it.code.equals(code, ignoreCase = true) } ?: LANGUAGES[1] // default English
    }

    fun isRtl(code: String): Boolean {
        return getLanguage(code).isRtl
    }
}
