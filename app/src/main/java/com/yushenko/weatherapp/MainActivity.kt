package com.yushenko.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.weatherapp.core.AppPreferencesManager
import com.weatherapp.core.PreferencesManager
import com.weatherapp.presentation.cities.CitiesScreen
import com.weatherapp.presentation.weather.WeatherScreen
import com.yushenko.weatherapp.ui.theme.WeatherAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesManager: AppPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                WeatherApp(preferencesManager)
            }
        }
    }
}

@Composable
fun WeatherApp(preferencesManager: PreferencesManager) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "weather") {
        composable("weather") {
            WeatherScreen(
                preferencesManager = preferencesManager,
                onNavigateToCities = { navController.navigate("cities") }
            )
        }
        composable("cities") {
            CitiesScreen(
                preferencesManager = preferencesManager,
                onCitySelected = { navController.popBackStack() }
            )
        }
    }
}