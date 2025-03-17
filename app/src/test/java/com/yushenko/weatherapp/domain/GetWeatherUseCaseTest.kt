package com.yushenko.weatherapp.domain

import com.weatherapp.domain.model.Weather
import com.weatherapp.domain.repository.WeatherRepository
import com.weatherapp.domain.usecase.GetWeatherUseCase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class GetWeatherUseCaseTest {

    @Mock
    private lateinit var weatherRepository: WeatherRepository

    private lateinit var useCase: GetWeatherUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        useCase = GetWeatherUseCase(weatherRepository)
    }

    @Test
    fun `invoke returns weather from repository`() = runTest {
        // Arrange
        val city = "London"
        val weather = Weather(
            cityName = city,
            temperature = 15f,
            feelsLike = 13f,
            humidity = 80,
            description = "cloudy",
            icon = "04d",
            windSpeed = 5.5f,
            pressure = 1013,
            sunrise = 1633069200, // Example: 07:00 UTC
            sunset = 1633112400   // Example: 18:00 UTC
        )
        `when`(weatherRepository.getWeather(city)).thenReturn(weather)

        //Act
        val result = useCase(city)

        // Assert
        assertEquals(weather, result)
    }

    @Test(expected = Exception::class)
    fun `invoke throw exception on repository failure`() = runTest {
        // Arrange
        val city = "London"
        `when`(weatherRepository.getWeather(city)).thenThrow(RuntimeException("Repository error"))

        // Act
        useCase(city)

        // Assert: Ожидаем исключение
    }
}