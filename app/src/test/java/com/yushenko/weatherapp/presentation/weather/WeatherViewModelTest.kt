package com.yushenko.weatherapp.presentation.weather

import com.weatherapp.domain.model.Weather
import com.weatherapp.domain.usecase.GetWeatherUseCase
import com.weatherapp.presentation.weather.WeatherIntent
import com.weatherapp.presentation.weather.WeatherStateManager
import com.weatherapp.presentation.weather.WeatherViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class WeatherViewModelTest {

    @Mock
    private lateinit var getWeatherUseCase: GetWeatherUseCase

    private lateinit var viewModel: WeatherStateManager

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = WeatherViewModel(getWeatherUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadWeather updates state with weather data`() = runTest {
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
        `when`(getWeatherUseCase(city)).thenReturn(weather)

        // Act
        viewModel.onIntent(WeatherIntent.LoadWeather(city))

        // Assert
        val state = viewModel.state.first()
        assertEquals(false, state.isLoading)
        assertEquals(weather, state.weather)
        assertEquals(null, state.error)
    }

    @Test
    fun `loadWeather updates state with error on failure`() = runTest {
        // Arrange
        val city = "London"
        `when`(getWeatherUseCase(city)).thenThrow(RuntimeException("API error"))

        // Act
        viewModel.onIntent(WeatherIntent.LoadWeather(city))

        // Assert
        val state = viewModel.state.first()
        assertEquals(false, state.isLoading)
        assertEquals(null, state.weather)
        assertEquals("API error", state.error)
    }
}