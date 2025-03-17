package com.weatherapp.presentation.cities

import com.weatherapp.domain.model.City

data class CitiesState(
    val isLoading: Boolean = false,
    val suggestions: List<City> = emptyList(),
    val error: String? = null
)

sealed class CitiesIntent {
    data class SearchCities(val query: String) : CitiesIntent()
}

sealed class CitiesEffect {
    data class ShowError(val message: String) : CitiesEffect()
}