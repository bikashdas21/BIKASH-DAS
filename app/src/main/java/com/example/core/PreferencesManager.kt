package com.example.core

import android.content.Context
import android.content.SharedPreferences
import com.example.localization.CountryRegistry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.TimeZone
import java.util.UUID

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("zero_grid_prefs", Context.MODE_PRIVATE)

    private val initialCountry = prefs.getString(KEY_COUNTRY, null) ?: CountryRegistry.detectSystemCountry().code
    private val initialTz = prefs.getString(KEY_TIME_ZONE, null) ?: TimeZone.getDefault().id

    private val _country = MutableStateFlow(initialCountry)
    val country: StateFlow<String> = _country.asStateFlow()

    private val _timeZone = MutableStateFlow(initialTz)
    val timeZone: StateFlow<String> = _timeZone.asStateFlow()

    private val _language = MutableStateFlow(prefs.getString(KEY_LANGUAGE, "en") ?: "en")
    val language: StateFlow<String> = _language.asStateFlow()

    private val _is24Hour = MutableStateFlow(prefs.getBoolean(KEY_24_HOUR, true))
    val is24Hour: StateFlow<Boolean> = _is24Hour.asStateFlow()

    private val _isMetric = MutableStateFlow(prefs.getBoolean(KEY_METRIC, true))
    val isMetric: StateFlow<Boolean> = _isMetric.asStateFlow()

    private val _isLowBandwidth = MutableStateFlow(prefs.getBoolean(KEY_LOW_BANDWIDTH, false))
    val isLowBandwidth: StateFlow<Boolean> = _isLowBandwidth.asStateFlow()

    private val _isDarkMode = MutableStateFlow(prefs.getBoolean(KEY_DARK_MODE, true))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _isRelayMode = MutableStateFlow(prefs.getBoolean(KEY_RELAY_MODE, true))
    val isRelayMode: StateFlow<Boolean> = _isRelayMode.asStateFlow()

    private val _isBatterySaver = MutableStateFlow(prefs.getBoolean(KEY_BATTERY_SAVER, false))
    val isBatterySaver: StateFlow<Boolean> = _isBatterySaver.asStateFlow()

    private val _isGuestMode = MutableStateFlow(prefs.getBoolean(KEY_GUEST_MODE, true))
    val isGuestMode: StateFlow<Boolean> = _isGuestMode.asStateFlow()

    private val _isZeroGridActive = MutableStateFlow(prefs.getBoolean(KEY_ZERO_GRID_ACTIVE, true))
    val isZeroGridActive: StateFlow<Boolean> = _isZeroGridActive.asStateFlow()

    private val _isFirstLaunch = MutableStateFlow(prefs.getBoolean(KEY_FIRST_LAUNCH, true))
    val isFirstLaunch: StateFlow<Boolean> = _isFirstLaunch.asStateFlow()

    private val _familyPhone = MutableStateFlow(prefs.getString(KEY_FAMILY_PHONE, "") ?: "")
    val familyPhone: StateFlow<String> = _familyPhone.asStateFlow()

    private val _medicalPhone = MutableStateFlow(prefs.getString(KEY_MEDICAL_PHONE, "") ?: "")
    val medicalPhone: StateFlow<String> = _medicalPhone.asStateFlow()

    val myNodeId: String = prefs.getString(KEY_NODE_ID, null) ?: run {
        val newId = "NODE-" + UUID.randomUUID().toString().take(6).uppercase()
        prefs.edit().putString(KEY_NODE_ID, newId).apply()
        newId
    }

    val myDisplayName: String = prefs.getString(KEY_DISPLAY_NAME, "Freedom Node ($myNodeId)")
        ?: "Freedom Node ($myNodeId)"

    fun setCountry(countryCode: String) {
        prefs.edit().putString(KEY_COUNTRY, countryCode).apply()
        _country.value = countryCode
        val info = CountryRegistry.getCountryByCode(countryCode)
        _isMetric.value = info.isMetricDefault
        prefs.edit().putBoolean(KEY_METRIC, info.isMetricDefault).apply()
    }

    fun setTimeZone(tzId: String) {
        prefs.edit().putString(KEY_TIME_ZONE, tzId).apply()
        _timeZone.value = tzId
    }

    fun setLanguage(lang: String) {
        prefs.edit().putString(KEY_LANGUAGE, lang).apply()
        _language.value = lang
    }

    fun set24Hour(is24: Boolean) {
        prefs.edit().putBoolean(KEY_24_HOUR, is24).apply()
        _is24Hour.value = is24
    }

    fun setMetric(metric: Boolean) {
        prefs.edit().putBoolean(KEY_METRIC, metric).apply()
        _isMetric.value = metric
    }

    fun setLowBandwidth(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_LOW_BANDWIDTH, enabled).apply()
        _isLowBandwidth.value = enabled
    }

    fun setDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
        _isDarkMode.value = enabled
    }

    fun setRelayMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_RELAY_MODE, enabled).apply()
        _isRelayMode.value = enabled
    }

    fun setBatterySaver(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BATTERY_SAVER, enabled).apply()
        _isBatterySaver.value = enabled
    }

    fun setGuestMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_GUEST_MODE, enabled).apply()
        _isGuestMode.value = enabled
    }

    fun setZeroGridActive(active: Boolean) {
        prefs.edit().putBoolean(KEY_ZERO_GRID_ACTIVE, active).apply()
        _isZeroGridActive.value = active
    }

    fun completeFirstLaunch(asGuest: Boolean) {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
        _isFirstLaunch.value = false
        setGuestMode(asGuest)
    }

    fun resetFirstLaunch() {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, true).apply()
        _isFirstLaunch.value = true
    }

    fun setDisplayName(name: String) {
        prefs.edit().putString(KEY_DISPLAY_NAME, name).apply()
    }

    fun setFamilyPhone(phone: String) {
        prefs.edit().putString(KEY_FAMILY_PHONE, phone).apply()
        _familyPhone.value = phone
    }

    fun setMedicalPhone(phone: String) {
        prefs.edit().putString(KEY_MEDICAL_PHONE, phone).apply()
        _medicalPhone.value = phone
    }

    companion object {
        private const val KEY_COUNTRY = "key_country"
        private const val KEY_TIME_ZONE = "key_time_zone"
        private const val KEY_LANGUAGE = "key_language"
        private const val KEY_24_HOUR = "key_24_hour"
        private const val KEY_METRIC = "key_metric"
        private const val KEY_LOW_BANDWIDTH = "key_low_bandwidth"
        private const val KEY_DARK_MODE = "key_dark_mode"
        private const val KEY_RELAY_MODE = "key_relay_mode"
        private const val KEY_BATTERY_SAVER = "key_battery_saver"
        private const val KEY_GUEST_MODE = "key_guest_mode"
        private const val KEY_ZERO_GRID_ACTIVE = "key_zero_grid_active"
        private const val KEY_FIRST_LAUNCH = "key_first_launch"
        private const val KEY_NODE_ID = "key_node_id"
        private const val KEY_DISPLAY_NAME = "key_display_name"
        private const val KEY_FAMILY_PHONE = "key_family_phone"
        private const val KEY_MEDICAL_PHONE = "key_medical_phone"
    }
}
