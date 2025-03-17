package com.weatherapp.presentation.weather

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.weatherapp.core.PreferencesManager
import com.weatherapp.domain.model.Weather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WeatherScreen(
    viewModel: WeatherStateManager = hiltViewModel<WeatherViewModel>(),
    preferencesManager: PreferencesManager,
    onNavigateToCities: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val currentCity by remember { derivedStateOf { preferencesManager.getCurrentCity() } }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is WeatherEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(currentCity) {
        currentCity?.let { viewModel.onIntent(WeatherIntent.LoadWeather(it)) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xff94b8ff))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Current Weather",
                    fontSize = 24.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = onNavigateToCities,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726))
                ) {
                    Text("Change City", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when {
                state.isLoading -> CircularProgressIndicator(color = Color.White)
                state.weather != null -> {
                    state.weather?.let { weather ->
                        WeatherMainInfo(weather = weather)

                        Spacer(modifier = Modifier.height(24.dp))

                        WeatherDetails(weather = weather)
                    }
                }

                state.error != null -> Text(
                    text = "Error: ${state.error}",
                    color = Color.Red,
                    fontSize = 18.sp
                )

                currentCity == null -> Text(
                    text = "Please select a city",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun WeatherMainInfo(weather: Weather) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = weather.cityName,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "${weather.temperature.toInt()}°C",
            fontSize = 64.sp,
            fontWeight = FontWeight.Light,
            color = Color.White
        )
        AsyncImage(
            model = "https://openweathermap.org/img/wn/${weather.icon}@2x.png",
            contentDescription = "Weather icon",
            modifier = Modifier.size(100.dp)
        )
        Text(
            text = weather.description.replaceFirstChar { it.uppercase() },
            fontSize = 20.sp,
            color = Color.White
        )
    }
}

@Composable
fun WeatherDetails(weather: Weather) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Details",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherDetailCard("Feels Like", "${weather.feelsLike.toInt()}°C")
            WeatherDetailCard("Humidity", "${weather.humidity}%")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherDetailCard("Wind", "${weather.windSpeed} m/s")
            WeatherDetailCard("Pressure", "${weather.pressure} hPa")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherDetailCard("Sunrise", weather.sunrise.toTimeString())
            WeatherDetailCard("Sunset", weather.sunset.toTimeString())
        }
    }
}

@Composable
fun WeatherDetailCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(80.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

fun Long.toTimeString(): String {
    val date = Date(this * 1000)
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(date)
}

@Preview(showBackground = true)
@Composable
fun WeatherScreenPreview() {
    WeatherScreen(
        viewModel = object : WeatherStateManager {
            override val state = MutableStateFlow(
                WeatherState(
                    weather = Weather(
                        cityName = "London",
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
                )
            )
            override val effect = MutableSharedFlow<WeatherEffect>()
            override fun onIntent(intent: WeatherIntent) {}
        },
        preferencesManager = object : PreferencesManager {
            override fun saveCity(city: String) {}
            override fun getRecentCities(): List<String> = listOf("London", "Paris")
            override fun recentCitiesFlow(): Flow<List<String>> = flowOf(listOf("London", "Paris"))
            override fun clearRecentCities() {}
            override fun setCurrentCity(city: String) {}
            override fun getCurrentCity(): String = "London"
        },
        onNavigateToCities = {}
    )
}