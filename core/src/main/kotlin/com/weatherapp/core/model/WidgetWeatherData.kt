package com.weatherapp.core.model

data class WidgetWeatherData(
    val cityName: String,
    val temperature: Float,
    val description: String,
    val icon: String
)