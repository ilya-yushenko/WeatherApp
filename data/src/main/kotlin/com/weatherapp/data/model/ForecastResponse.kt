package com.weatherapp.data.model

import com.weatherapp.domain.model.Forecast

data class ForecastResponse(
    val list: List<ForecastItem>
)

data class ForecastItem(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>
)

fun ForecastItem.toDomainForecast(): Forecast {
    return Forecast(
        date = dt,
        minTemp = main.temp_min,
        maxTemp = main.temp_max,
        description = weather.first().description,
        icon = weather.first().icon
    )
}