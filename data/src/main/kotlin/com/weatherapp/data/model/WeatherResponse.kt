package com.weatherapp.data.model

data class WeatherResponse(
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind,
    val sys: Sys,
    val name: String
)

data class Main(
    val temp: Float,
    val feels_like: Float,
    val temp_min: Float,
    val temp_max: Float,
    val humidity: Int,
    val pressure: Int
)

data class Weather(
    val description: String,
    val icon: String
)

data class Wind(
    val speed: Float
)

data class Sys(
    val sunrise: Long,
    val sunset: Long
)

internal fun WeatherResponse.toDomainWeather(): com.weatherapp.domain.model.Weather {
    return com.weatherapp.domain.model.Weather(
        cityName = name,
        temperature = main.temp,
        feelsLike = main.feels_like,
        humidity = main.humidity,
        description = weather.first().description,
        icon = weather.first().icon,
        windSpeed = wind.speed,
        pressure = main.pressure,
        sunrise = sys.sunrise,
        sunset = sys.sunset
    )
}