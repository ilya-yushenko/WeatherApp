package com.weatherapp.core

import android.graphics.Bitmap
import com.weatherapp.core.model.WidgetWeatherData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockPreferencesManager : PreferencesManager {
    override fun saveCity(city: String) {}
    override fun getRecentCities(): List<String> = listOf("Kyiv", "Tokyo", "New York")
    override fun recentCitiesFlow(): Flow<List<String>> = flowOf(listOf("Kyiv", "Tokyo", "New York"))
    override fun clearRecentCities() {}
    override fun setCurrentCity(city: String) {}
    override fun getCurrentCity(): String = "London"
    override fun setRainAlertEnabled(enabled: Boolean) {}
    override fun getRainAlertEnabled(): Boolean = true
    override fun setWindAlertEnabled(enabled: Boolean) {}
    override fun getWindAlertEnabled(): Boolean = false
    override fun setWindThreshold(threshold: Float) {}
    override fun getWindThreshold(): Float = 25f
    override fun saveWidgetWeather(weather: WidgetWeatherData, bitmap: Bitmap?) {}
    override fun getWidgetWeather(): WidgetWeatherData? = null
    override fun getWidgetBitmap(): Bitmap? = null
}