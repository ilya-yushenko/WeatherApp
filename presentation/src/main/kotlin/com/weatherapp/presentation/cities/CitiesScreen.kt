package com.weatherapp.presentation.cities

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.weatherapp.core.PreferencesManager
import com.weatherapp.domain.model.City
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf

@Composable
fun CitiesScreen(
    viewModel: CitiesStateManager = hiltViewModel<CitiesViewModel>(),
    preferencesManager: PreferencesManager,
    onBack: () -> Unit
) {
    val backgroundColor = Color(0xff6b9cff)
    val state by viewModel.state.collectAsState()
    val recentCities by preferencesManager.recentCitiesFlow()
        .collectAsState(initial = preferencesManager.getRecentCities())
    var query by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is CitiesEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(query) {
        if (query.isNotEmpty()) {
            viewModel.onIntent(CitiesIntent.SearchCities(query))
        }
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onBack() }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Open settings",
                    tint = Color.White
                )
            }
            Text(
                text = "Select City",
                fontSize = 22.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search city") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.LightGray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading) {
            CircularProgressIndicator(color = Color.White)
        } else if (state.suggestions.isNotEmpty()) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.suggestions) { city ->
                    CityItem(city = city, onClick = {
                        preferencesManager.setCurrentCity(city.name)
                        preferencesManager.saveCity(city.name)
                        onBack()
                    })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Recent Cities",
            fontSize = 20.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(recentCities) { city ->
                CityItem(city = city, onClick = {
                    preferencesManager.setCurrentCity(city)
                    preferencesManager.saveCity(city)
                    onBack()
                })
            }
        }

        if (recentCities.isNotEmpty()) {
            Button(
                onClick = { preferencesManager.clearRecentCities() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
            ) {
                Text("Clear Recent Cities", color = Color.White)
            }
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun CityItem(city: City, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = "${city.name}, ${city.country}",
            fontSize = 18.sp,
            color = Color.White,
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Composable
fun CityItem(city: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = city,
            fontSize = 18.sp,
            color = Color.White,
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CitiesScreenPreview() {
    CitiesScreen(
        viewModel = object : CitiesStateManager {
            override val state = MutableStateFlow(
                CitiesState(
                    suggestions = listOf(
                        City(name = "London", country = "UK"),
                        City(name = "Paris", country = "FR"),
                        City(name = "Berlin", country = "DE")
                    )
                )
            )
            override val effect = MutableSharedFlow<CitiesEffect>()
            override fun onIntent(intent: CitiesIntent) {}
        },
        preferencesManager = object : PreferencesManager {
            override fun saveCity(city: String) {}
            override fun getRecentCities(): List<String> = listOf("Kyiv", "Tokyo", "New York")
            override fun recentCitiesFlow(): Flow<List<String>> =
                flowOf(listOf("Kyiv", "Tokyo", "New York"))

            override fun clearRecentCities() {}
            override fun setCurrentCity(city: String) {}
            override fun getCurrentCity(): String = "Kyiv"
        },
        onBack = {}
    )
}