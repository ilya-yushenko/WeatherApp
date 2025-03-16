package com.weatherapp.domain.repository

import com.weatherapp.domain.model.Weather

interface WeatherRepository {
    suspend fun getWeather(city: String): Weather
}