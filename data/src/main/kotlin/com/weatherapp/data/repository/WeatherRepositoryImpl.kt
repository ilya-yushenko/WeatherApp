package com.weatherapp.data.repository

import com.weatherapp.core.AppConstants
import com.weatherapp.data.api.WeatherApi
import com.weatherapp.data.model.toDomainWeather
import com.weatherapp.domain.model.Weather
import com.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi
) : WeatherRepository {

    override suspend fun getWeather(city: String): Weather {
        return api.getWeather(city, AppConstants.APP_ID).toDomainWeather()
    }
}
