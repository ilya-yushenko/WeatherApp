package com.weatherapp.domain.model

data class Weather(
    val cityName: String,
    val temperature: Float,
    val feelsLike: Float,
    val humidity: Int,
    val description: String,
    val icon: String,
    val windSpeed: Float,
    val pressure: Int,
    val sunrise: Long,
    val sunset: Long
)