package com.weatherapp.presentation.forecast

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.weatherapp.core.MockPreferencesManager
import com.weatherapp.core.PreferencesManager
import com.weatherapp.domain.model.Forecast
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ForecastScreen(
    viewModel: ForecastStateManager = hiltViewModel<ForecastViewModel>(),
    preferencesManager: PreferencesManager,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val currentCity by remember { derivedStateOf { preferencesManager.getCurrentCity() } }
    val backgroundColor = Color(0xff6b9cff)
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is ForecastEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(currentCity) {
        currentCity?.let { viewModel.onIntent(ForecastIntent.LoadForecast(it)) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = "5-Day Forecast",
                fontSize = 22.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    currentCity?.let { viewModel.onIntent(ForecastIntent.LoadForecast(it)) }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726))
            ) {
                Text("Update", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            state.isLoading -> CircularProgressIndicator(color = Color.White)
            state.forecast.isNotEmpty() -> {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 48.dp)
                ) {
                    items(state.forecast) { forecast ->
                        ForecastItem(forecast = forecast)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            state.error != null -> Text(
                text = "Error: ${state.error}",
                color = Color.Red,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun ForecastItem(forecast: Forecast) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = "https://openweathermap.org/img/wn/${forecast.icon}@2x.png",
                contentDescription = "Weather icon",
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = forecast.date.toDateString(),
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${forecast.minTemp.toInt()}°C / ${forecast.maxTemp.toInt()}°C",
                    fontSize = 16.sp,
                    color = Color.White
                )
                Text(
                    text = forecast.description.replaceFirstChar { it.uppercase() },
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    }
}

fun Long.toDateString(): String {
    val date = Date(this * 1000)
    val sdf = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
    return sdf.format(date)
}

@Preview(showBackground = true)
@Composable
fun ForecastScreenPreview() {
    ForecastScreen(
        viewModel = object : ForecastStateManager {
            override val state = MutableStateFlow(
                ForecastState(
                    forecast = listOf(
                        Forecast(1633069200, 10f, 15f, "cloudy", "04d"),
                        Forecast(1633155600, 12f, 17f, "rain", "10d")
                    )
                )
            )
            override val effect = MutableSharedFlow<ForecastEffect>()
            override fun onIntent(intent: ForecastIntent) {}
        },
        preferencesManager = MockPreferencesManager(),
        onBack = {}
    )
}