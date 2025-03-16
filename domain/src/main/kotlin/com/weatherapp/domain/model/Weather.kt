package com.weatherapp.domain.model

data class Weather(
    val cityName: String,
    val temperature: Float,
    val humidity: Int,
    val description: String,
    val icon: String
)