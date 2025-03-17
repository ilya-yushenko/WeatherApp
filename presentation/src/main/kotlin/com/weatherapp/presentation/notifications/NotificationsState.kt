package com.weatherapp.presentation.notifications

data class NotificationsState(
    val rainAlertEnabled: Boolean = false,
    val windAlertEnabled: Boolean = false,
    val windThreshold: Float = 10f
)

sealed class NotificationsIntent {
    data class ToggleRainAlert(val enabled: Boolean) : NotificationsIntent()
    data class ToggleWindAlert(val enabled: Boolean, val threshold: Float) : NotificationsIntent()
}

sealed class NotificationsEffect {
    data class ShowError(val message: String) : NotificationsEffect()
}