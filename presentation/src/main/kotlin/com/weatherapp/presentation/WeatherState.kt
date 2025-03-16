package com.weatherapp.presentation

import com.weatherapp.domain.model.Weather

data class WeatherState(
    val isLoading: Boolean = false,
    val weather: Weather? = null,
    val error: String? = null
)

sealed class WeatherIntent {
    data class LoadWeather(val city: String) : WeatherIntent()
}

sealed class WeatherEffect {
    data class ShowError(val message: String) : WeatherEffect()
}