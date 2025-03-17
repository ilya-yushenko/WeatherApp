package com.weatherapp.core

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferencesManager @Inject constructor(
    @ApplicationContext context: Context
) : PreferencesManager {
    val prefs = context.getSharedPreferences(AppConstants.APP_PREFS, Context.MODE_PRIVATE)
    private val _recentCitiesFlow = MutableStateFlow(getRecentCities())

    override fun saveCity(city: String) {
        val cities = getRecentCities().toMutableList()
        cities.remove(city)
        cities.add(0, city)
        if (cities.size > AppConstants.COUNT_RECENT_CITY) cities.removeAt(cities.lastIndex)
        prefs.edit { putString(AppConstants.APP_PREFS_RECENT_CITIES, cities.joinToString(",")) }
        _recentCitiesFlow.value = getRecentCities()
    }

    override fun getRecentCities(): List<String> {
        val citiesString = prefs.getString(AppConstants.APP_PREFS_RECENT_CITIES, "") ?: ""
        return if (citiesString.isEmpty()) emptyList() else citiesString.split(",")
    }

    override fun recentCitiesFlow(): Flow<List<String>> = _recentCitiesFlow

    override fun clearRecentCities() {
        prefs.edit { remove(AppConstants.APP_PREFS_RECENT_CITIES) }
        _recentCitiesFlow.value = emptyList()
    }

    override fun setCurrentCity(city: String) {
        prefs.edit { putString(AppConstants.APP_PREFS_CURRENT_CITY, city) }
    }

    override fun getCurrentCity(): String? {
        return prefs.getString(AppConstants.APP_PREFS_CURRENT_CITY, null)
    }

    override fun setRainAlertEnabled(enabled: Boolean) {
        prefs.edit { putBoolean("rain_alert", enabled) }
    }

    override fun getRainAlertEnabled(): Boolean {
        return prefs.getBoolean("rain_alert", false)
    }

    override fun setWindAlertEnabled(enabled: Boolean) {
        prefs.edit { putBoolean("wind_alert", enabled) }
    }

    override fun getWindAlertEnabled(): Boolean {
        return prefs.getBoolean("wind_alert", false)
    }

    override fun setWindThreshold(threshold: Float) {
        prefs.edit { putFloat("wind_threshold", threshold) }
    }

    override fun getWindThreshold(): Float {
        return prefs.getFloat("wind_threshold", 10f)
    }
}