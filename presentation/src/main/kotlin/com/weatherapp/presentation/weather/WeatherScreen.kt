package com.weatherapp.presentation.weather

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.weatherapp.core.MockPreferencesManager
import com.weatherapp.core.PreferencesManager
import com.weatherapp.domain.model.Weather
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WeatherScreen(
    viewModel: WeatherStateManager = hiltViewModel<WeatherViewModel>(),
    preferencesManager: PreferencesManager,
    onNavigateToCities: () -> Unit,
    onNavigateToForecast: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val backgroundColor = Color(0xff6b9cff)
    val state by viewModel.state.collectAsState()
    val currentCity by remember { derivedStateOf { preferencesManager.getCurrentCity() } }
    val context = LocalContext.current
    var isPressureInMmHg by remember { mutableStateOf(false) }
    var isTempInFahrenheit by remember { mutableStateOf(false) }
    var isWindInKmh by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Menu",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    DrawerItem("Forecast", Icons.Default.CalendarToday, onNavigateToForecast)
                    DrawerItem("Favorites", Icons.Default.Favorite, onNavigateToFavorites)
                    DrawerItem(
                        "Notifications",
                        Icons.Default.Notifications,
                        onNavigateToNotifications
                    )
                    DrawerItem("Settings", Icons.Default.Settings, onNavigateToSettings)

                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Units",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Temperature in ${if (isTempInFahrenheit) "°F" else "°C"}",
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Switch(
                            checked = isTempInFahrenheit,
                            onCheckedChange = { isTempInFahrenheit = it }
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Wind in ${if (isWindInKmh) "km/h" else "m/s"}",
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Switch(
                            checked = isWindInKmh,
                            onCheckedChange = { isWindInKmh = it }
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pressure in ${if (isPressureInMmHg) "mmHg" else "hPa"}",
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Switch(
                            checked = isPressureInMmHg,
                            onCheckedChange = { isPressureInMmHg = it },
                        )
                    }
                }
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { scope.launch { drawerState.open() } }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Open settings",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Current Weather",
                        fontSize = 22.sp,
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
                            WeatherMainInfo(
                                weather = weather,
                                isTempInFahrenheit = isTempInFahrenheit
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                WeatherDetails(
                                    weather = weather,
                                    isPressureInMmHg = isPressureInMmHg,
                                    isTempInFahrenheit = isTempInFahrenheit,
                                    isWindInKmh = isWindInKmh
                                )
                            }
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
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
fun WeatherMainInfo(weather: Weather, isTempInFahrenheit: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = weather.cityName,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = if (isTempInFahrenheit) "${(weather.temperature * 9 / 5 + 32).toInt()}°F" else "${weather.temperature.toInt()}°C",
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
fun WeatherDetails(
    weather: Weather,
    isPressureInMmHg: Boolean,
    isTempInFahrenheit: Boolean,
    isWindInKmh: Boolean
) {
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
            WeatherDetailCard(
                icon = Icons.Default.Thermostat,
                title = "Feels Like",
                value = if (isTempInFahrenheit) "${(weather.feelsLike * 9 / 5 + 32).toInt()}°F" else "${weather.feelsLike.toInt()}°C"
            )
            WeatherDetailCard(
                icon = Icons.Default.WaterDrop,
                title = "Humidity",
                value = "${weather.humidity}%"
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherDetailCard(
                icon = Icons.Default.Air,
                title = "Wind",
                value = if (isWindInKmh) "${(weather.windSpeed * 3.6).toInt()} km/h" else "${weather.windSpeed} m/s"
            )
            WeatherDetailCard(
                icon = Icons.Default.ArrowDownward,
                title = "Pressure",
                value = if (isPressureInMmHg) "${(weather.pressure * 0.75006).toInt()} mmHg" else "${weather.pressure} hPa"
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherDetailCard(
                icon = Icons.Default.WbSunny,
                title = "Sunrise",
                value = weather.sunrise.toTimeString()
            )
            WeatherDetailCard(
                icon = Icons.Default.WbSunny,
                title = "Sunset",
                value = weather.sunset.toTimeString()
            )
        }
    }
}

@Composable
fun WeatherDetailCard(icon: ImageVector, title: String, value: String) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(100.dp),
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
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
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

@Composable
fun DrawerItem(text: String, icon: ImageVector, onClick: (() -> Unit)?) {
    if (onClick != null) {
        Row(
            modifier = Modifier
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = text, tint = Color.Black)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = text, fontSize = 18.sp, color = Color.Black)
        }
    } else {
        Text(text = text, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
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
        preferencesManager = MockPreferencesManager(),
        onNavigateToCities = {},
        onNavigateToForecast = {},
        onNavigateToFavorites = {},
        onNavigateToNotifications = {},
        onNavigateToSettings = {}
    )
}