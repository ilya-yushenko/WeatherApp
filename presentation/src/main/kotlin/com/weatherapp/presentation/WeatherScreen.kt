package com.weatherapp.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun WeatherScreen(viewModel: WeatherViewModel = hiltViewModel()) {
    val current = LocalContext.current
    val state by viewModel.state.collectAsState()
    val effect by viewModel.effect.collectAsState(initial = null)

    LaunchedEffect(effect) {
        effect?.let {
            when (it) {
                is WeatherEffect.ShowError -> Toast.makeText(
                    current,
                    it.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        var city by remember { mutableStateOf("") }

        TextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("Enter city") }
        )
        Button(onClick = { viewModel.onIntent(WeatherIntent.LoadWeather(city)) }) {
            Text("Get Weather")
        }

        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            state.weather?.let { weather ->
                Text("City: ${weather.cityName}")
                Text("Temp: ${weather.temperature}°C")
                Text("Description: ${weather.description}")
            }
            state.error?.let { Text("Error: $it") }
        }
    }
}