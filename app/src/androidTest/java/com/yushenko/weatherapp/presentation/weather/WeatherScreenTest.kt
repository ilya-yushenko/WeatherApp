package com.yushenko.weatherapp.presentation.weather

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.weatherapp.core.AppPreferencesManager
import com.weatherapp.domain.model.Weather
import com.weatherapp.domain.usecase.GetWeatherUseCase
import com.weatherapp.presentation.weather.WeatherScreen
import com.weatherapp.presentation.weather.WeatherState
import com.weatherapp.presentation.weather.WeatherStateManager
import com.weatherapp.presentation.weather.WeatherViewModel
import com.yushenko.weatherapp.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

@HiltAndroidTest
class WeatherScreenTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var viewModel: WeatherStateManager
    private lateinit var preferencesManager: AppPreferencesManager

    @Before
    fun setUp() {
        hiltRule.inject()
        preferencesManager = mock(AppPreferencesManager::class.java)
        viewModel = WeatherViewModel(mock(GetWeatherUseCase::class.java))
    }

    @Test
    fun weatherScreenDisplaysWeatherData() {
        // Arrange
        val weather = Weather(
            cityName = "London",
            temperature = 15f,
            feelsLike = 13f,
            humidity = 80,
            description = "Cloudy",
            icon = "04d",
            windSpeed = 5.5f,
            pressure = 1013,
            sunrise = 1633069200, // Example: 07:00 UTC
            sunset = 1633112400   // Example: 18:00 UTC
        )
        viewModel = object : WeatherViewModel(mock()) {
            override val state = MutableStateFlow(WeatherState(weather = weather))
        }

        // Act
        composeTestRule.setContent {
            WeatherScreen(
                viewModel = viewModel,
                preferencesManager = preferencesManager,
                onNavigateToCities = { },
                onNavigateToForecast = { },
                onNavigateToFavorites = {},
                onNavigateToNotifications = { },
                onNavigateToSettings = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithText("London").assertIsDisplayed()
        composeTestRule.onNodeWithText("15°C").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cloudy").assertIsDisplayed()
    }
}