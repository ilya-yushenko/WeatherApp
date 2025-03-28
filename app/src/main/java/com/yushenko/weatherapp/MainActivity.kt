package com.yushenko.weatherapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.weatherapp.core.AppPreferencesManager
import com.weatherapp.core.PreferencesManager
import com.weatherapp.presentation.cities.CitiesScreen
import com.weatherapp.presentation.forecast.ForecastScreen
import com.weatherapp.presentation.notifications.NotificationsScreen
import com.weatherapp.presentation.weather.WeatherScreen
import com.yushenko.weatherapp.ui.theme.WeatherAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesManager: AppPreferencesManager

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
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
                onNavigateToCities = { navController.navigate("cities") },
                onNavigateToForecast = { navController.navigate("forecast") },
                onNavigateToFavorites = {},
                onNavigateToNotifications = { navController.navigate("notifications") },
                onNavigateToSettings = {}
            )
        }
        composable("cities") {
            CitiesScreen(
                preferencesManager = preferencesManager,
                onBack = { navController.popBackStack() }
            )
        }
        composable("forecast") {
            ForecastScreen(
                preferencesManager = preferencesManager,
                onBack = { navController.popBackStack() }
            )
        }
        composable("notifications") {
            NotificationsScreen(
                preferencesManager = preferencesManager,
                onBack = { navController.popBackStack() }
            )
        }
    }
}