package com.weatherapp.domain.usecase

import com.weatherapp.domain.model.Forecast
import com.weatherapp.domain.repository.WeatherRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class GetForecastUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(city: String): List<Forecast> {
        val forecast = repository.getForecast(city)
        return aggregateForecastByDay(forecast)
    }

    private fun aggregateForecastByDay(rawForecast: List<Forecast>): List<Forecast> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return rawForecast.groupBy { dateFormat.format(Date(it.date * 1000)) }
            .map { (dateStr, forecasts) ->
                val minTemp = forecasts.minOf { it.minTemp }
                val maxTemp = forecasts.maxOf { it.maxTemp }
                val lastForecast =
                    forecasts.last()
                Forecast(
                    date = dateFormat.parse(dateStr)!!.time / 1000,
                    minTemp = minTemp,
                    maxTemp = maxTemp,
                    description = lastForecast.description,
                    icon = lastForecast.icon
                )
            }
            .sortedBy { it.date }
    }
}