package com.weatherapp.domain.repository

import com.weatherapp.domain.model.City

interface CityRepository {
    suspend fun getCitySuggestions(query: String): List<City>
}