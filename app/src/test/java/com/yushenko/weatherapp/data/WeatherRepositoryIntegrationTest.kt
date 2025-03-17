package com.yushenko.weatherapp.data

import com.weatherapp.data.api.WeatherApi
import com.weatherapp.data.repository.WeatherRepositoryImpl
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherRepositoryIntegrationTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var weatherApi: WeatherApi
    private lateinit var repository: WeatherRepositoryImpl

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        weatherApi = retrofit.create(WeatherApi::class.java)
        repository = WeatherRepositoryImpl(weatherApi)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getWeather fetches data from API`() = runTest {
        // Arrange
        val jsonResponse = """
            {
                "main": {
                    "temp": 15.0,
                    "feels_like": 11.72,
                    "temp_min": 11.01,
                    "temp_max": 13.9,
                    "pressure": 1016,
                    "humidity": 77,
                    "sea_level": 1016,
                    "grnd_level": 1012
                },
                "weather": [{"description": "Cloudy", "icon": "04d"}],
                "wind": {"speed": 5.0},
                "sys": {
                    "sunrise": 1742221036,
                    "sunset": 1742264316
                },
                "name": "London"
            }
        """.trimIndent()
        mockWebServer.enqueue(MockResponse().setBody(jsonResponse).setResponseCode(200))

        // Act
        val result = repository.getWeather("London")

        // Assert
        assertEquals("London", result.cityName)
        assertEquals(15.0f, result.temperature)
        assertEquals("Cloudy", result.description)
        assertEquals("04d", result.icon)
        assertEquals(5.0f, result.windSpeed)
    }
}