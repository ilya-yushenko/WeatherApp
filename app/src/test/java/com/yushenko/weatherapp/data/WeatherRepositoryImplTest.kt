package com.yushenko.weatherapp.data

import com.weatherapp.core.AppConstants
import com.weatherapp.data.api.WeatherApi
import com.weatherapp.data.model.Main
import com.weatherapp.data.model.Sys
import com.weatherapp.data.model.Weather
import com.weatherapp.data.model.WeatherResponse
import com.weatherapp.data.model.Wind
import com.weatherapp.data.repository.WeatherRepositoryImpl
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class WeatherRepositoryImplTest {

    @Mock
    private lateinit var weatherApi: WeatherApi

    private lateinit var repository: WeatherRepositoryImpl

    private val appId = AppConstants.APP_ID

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = WeatherRepositoryImpl(weatherApi)
    }

    @Test
    fun `getWeather returns weather data successfully`() = runTest {
        // Arrange
        val city = "London"
        val weatherResponse = WeatherResponse(
            main = Main(
                temp = 15.0f,
                feels_like = 14.0f,
                temp_min = 13.0f,
                temp_max = 16.0f,
                humidity = 80,
                pressure = 1013
            ),
            weather = listOf(Weather(description = "Cloudy", icon = "04d")),
            wind = Wind(speed = 5.0f),
            sys = Sys(sunrise = 1633069200L, sunset = 1633112400L),
            name = city
        )

        `when`(weatherApi.getWeather(city, appId)).thenReturn(weatherResponse)

        // Act
        val result = repository.getWeather(city)

        assertEquals(city, result.cityName)
        assertEquals(15.0f, result.temperature)
        assertEquals("Cloudy", result.description)
        assertEquals("04d", result.icon)
        assertEquals(5.0f, result.windSpeed)
    }

    @Test(expected = Exception::class)
    fun `getWeather throws exception on API failure`() = runTest {
        // Arrange
        val city = "London"
        `when`(weatherApi.getWeather(city, appId)).thenThrow(RuntimeException("API error"))

        // Act
        repository.getWeather(city)

        // Assert: Ожидаем исключение
    }
}