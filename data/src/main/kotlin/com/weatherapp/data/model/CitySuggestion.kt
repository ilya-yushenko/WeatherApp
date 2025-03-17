package com.weatherapp.data.model

import com.weatherapp.domain.model.City

data class CitySuggestion(
    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double
)

fun CitySuggestion.toDomainCity(): City {
    return City(name = name, country = country)
}