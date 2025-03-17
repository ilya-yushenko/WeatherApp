package com.weatherapp.domain.model

data class WidgetWeather(
    val cityName: String,
    val temperature: Float,
    val description: String,
    val icon: String
)