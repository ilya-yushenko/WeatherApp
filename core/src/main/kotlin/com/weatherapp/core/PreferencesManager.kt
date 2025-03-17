package com.weatherapp.core

import kotlinx.coroutines.flow.Flow

interface PreferencesManager {
    fun saveCity(city: String)
    fun getRecentCities(): List<String>
    fun recentCitiesFlow(): Flow<List<String>>
    fun clearRecentCities()
    fun setCurrentCity(city: String)
    fun getCurrentCity(): String?
    fun setRainAlertEnabled(enabled: Boolean)
    fun getRainAlertEnabled(): Boolean
    fun setWindAlertEnabled(enabled: Boolean)
    fun getWindAlertEnabled(): Boolean
    fun setWindThreshold(threshold: Float)
    fun getWindThreshold(): Float
}