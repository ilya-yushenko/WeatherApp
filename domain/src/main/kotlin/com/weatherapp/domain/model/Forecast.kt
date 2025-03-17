package com.weatherapp.domain.model

data class Forecast(
    val date: Long,
    val minTemp: Float,
    val maxTemp: Float,
    val description: String,
    val icon: String
)