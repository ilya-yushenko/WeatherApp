package com.weatherapp.domain.usecase

import com.weatherapp.domain.model.WidgetWeather
import com.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class GetWidgetWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(city: String): WidgetWeather {
        val weather = repository.getWeather(city)
        return WidgetWeather(
            cityName = weather.cityName,
            temperature = weather.temperature,
            description = weather.description,
            icon = weather.icon
        )
    }
}