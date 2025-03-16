package com.weatherapp.domain.usecase

import com.weatherapp.domain.model.Weather
import com.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(city: String): Weather {
        return repository.getWeather(city)
    }
}