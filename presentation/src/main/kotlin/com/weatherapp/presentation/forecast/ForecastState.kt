package com.weatherapp.presentation.forecast

import com.weatherapp.domain.model.Forecast

data class ForecastState(
    val isLoading: Boolean = false,
    val forecast: List<Forecast> = emptyList(),
    val error: String? = null
)

sealed class ForecastIntent {
    data class LoadForecast(val city: String) : ForecastIntent()
}

sealed class ForecastEffect {
    data class ShowError(val message: String) : ForecastEffect()
}