package com.weatherapp.data.repository

import com.weatherapp.core.AppConstants
import com.weatherapp.data.api.WeatherApi
import com.weatherapp.data.model.toDomainCity
import com.weatherapp.domain.model.City
import com.weatherapp.domain.repository.CityRepository
import javax.inject.Inject

class CityRepositoryImpl @Inject constructor(
    private val api: WeatherApi
) : CityRepository {

    override suspend fun getCitySuggestions(query: String): List<City> {
        return api.getCitySuggestions(query, 5, AppConstants.APP_ID).map { it.toDomainCity() }
    }
}