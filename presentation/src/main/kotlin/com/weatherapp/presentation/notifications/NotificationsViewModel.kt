package com.weatherapp.presentation.notifications

import androidx.lifecycle.ViewModel
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.weatherapp.core.AppPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface NotificationsStateManager {
    val state: StateFlow<NotificationsState>
    val effect: SharedFlow<NotificationsEffect>
    fun onIntent(intent: NotificationsIntent)
}

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val preferencesManager: AppPreferencesManager,
    private val workManager: WorkManager,
) : ViewModel(), NotificationsStateManager {

    private val _state = MutableStateFlow(
        NotificationsState(
            rainAlertEnabled = preferencesManager.getRainAlertEnabled(),
            windAlertEnabled = preferencesManager.getWindAlertEnabled(),
            windThreshold = preferencesManager.getWindThreshold()
        )
    )
    override val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<NotificationsEffect>()
    override val effect: SharedFlow<NotificationsEffect> = _effect.asSharedFlow()

    override fun onIntent(intent: NotificationsIntent) {
        when (intent) {
            is NotificationsIntent.ToggleRainAlert -> toggleRainAlert(intent.enabled)
            is NotificationsIntent.ToggleWindAlert -> toggleWindAlert(
                intent.enabled,
                intent.threshold
            )
        }
    }

    private fun toggleRainAlert(enabled: Boolean) {
        _state.value = _state.value.copy(rainAlertEnabled = enabled)
        preferencesManager.setRainAlertEnabled(enabled)
        if (enabled || _state.value.windAlertEnabled) {
            scheduleWeatherCheck()
        } else {
            cancelWeatherCheck()
        }
    }

    private fun toggleWindAlert(enabled: Boolean, threshold: Float) {
        _state.value = _state.value.copy(windAlertEnabled = enabled, windThreshold = threshold)
        preferencesManager.setWindAlertEnabled(enabled)
        preferencesManager.setWindThreshold(threshold)
        if (enabled || _state.value.rainAlertEnabled) {
            scheduleWeatherCheck()
        } else {
            cancelWeatherCheck()
        }
    }

    private fun scheduleWeatherCheck() {
        val request = PeriodicWorkRequestBuilder<WeatherCheckWorker>(15, TimeUnit.MINUTES)
            .build()
        workManager.enqueueUniquePeriodicWork(
            "weather_check",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    private fun cancelWeatherCheck() {
        workManager.cancelUniqueWork("weather_check")
    }
}