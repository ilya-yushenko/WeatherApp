package com.weatherapp.core

import android.graphics.Bitmap
import com.weatherapp.core.model.WidgetWeatherData
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
    fun saveWidgetWeather(weather: WidgetWeatherData, bitmap: Bitmap?)
    fun getWidgetWeather(): WidgetWeatherData?
    fun getWidgetBitmap(): Bitmap?
}