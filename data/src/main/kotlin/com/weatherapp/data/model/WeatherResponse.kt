package com.weatherapp.data.model

data class WeatherResponse(
    val main: Main,
    val weather: List<Weather>,
    val name: String
)

data class Main(
    val temp: Float,
    val humidity: Int
)

data class Weather(
    val description: String,
    val icon: String
)

internal fun WeatherResponse.toDomainWeather(): com.weatherapp.domain.model.Weather {
    return com.weatherapp.domain.model.Weather(
        cityName = name,
        temperature = main.temp,
        humidity = main.humidity,
        description = weather.first().description,
        icon = weather.first().icon
    )
}