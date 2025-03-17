package com.weatherapp.core

import kotlinx.coroutines.flow.Flow

interface PreferencesManager {
    fun saveCity(city: String)
    fun getRecentCities(): List<String>
    fun recentCitiesFlow(): Flow<List<String>>
    fun clearRecentCities()
    fun setCurrentCity(city: String)
    fun getCurrentCity(): String?
}