package com.weatherapp.domain.usecase

import com.weatherapp.domain.model.City
import com.weatherapp.domain.repository.CityRepository
import javax.inject.Inject

class GetCitySuggestionsUseCase @Inject constructor(
    private val repository: CityRepository
) {
    suspend operator fun invoke(query: String): List<City> {
        return repository.getCitySuggestions(query)
    }
}