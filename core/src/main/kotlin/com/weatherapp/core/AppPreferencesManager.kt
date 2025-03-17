package com.weatherapp.core

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.content.edit
import com.weatherapp.core.model.WidgetWeatherData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.ByteArrayOutputStream
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

    override fun saveWidgetWeather(weather: WidgetWeatherData, bitmap: Bitmap?) {
        with(prefs.edit()) {
            putString("widget_city", weather.cityName)
            putFloat("widget_temp", weather.temperature)
            putString("widget_desc", weather.description)
            putString("widget_icon", weather.icon)
            bitmap?.let {
                val stream = ByteArrayOutputStream()
                it.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val byteArray = stream.toByteArray()
                putString("widget_bitmap", android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT))
            }
            apply()
        }
    }

    override fun getWidgetWeather(): WidgetWeatherData? {
        val city = prefs.getString("widget_city", null) ?: return null
        return WidgetWeatherData(
            cityName = city,
            temperature = prefs.getFloat("widget_temp", 0f),
            description = prefs.getString("widget_desc", "Unknown") ?: "Unknown",
            icon = prefs.getString("widget_icon", "01d") ?: "01d"
        )
    }

    override fun getWidgetBitmap(): Bitmap? {
        val encoded = prefs.getString("widget_bitmap", null) ?: return null
        val byteArray = android.util.Base64.decode(encoded, android.util.Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}