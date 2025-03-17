package com.weatherapp.presentation.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.weatherapp.core.AppPreferencesManager
import com.weatherapp.domain.usecase.GetWeatherUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class WeatherCheckWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val getWeatherUseCase: GetWeatherUseCase,
    private val preferencesManager: AppPreferencesManager
) : CoroutineWorker(context, params) {

    companion object {
        private const val CHANNEL_ID = "weather_notifications"
        private const val RAIN_NOTIFICATION_ID = 1
        private const val WIND_NOTIFICATION_ID = 2
    }

    override suspend fun doWork(): Result {
        val city = preferencesManager.getCurrentCity() ?: return Result.success()
        val weather = try {
            getWeatherUseCase(city)
        } catch (e: Exception) {
            return Result.retry()
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager)

        if (preferencesManager.getRainAlertEnabled() && weather.description.contains(
                "rain",
                ignoreCase = true
            )
        ) {
            sendRainNotification(notificationManager, weather.cityName)
        }

        if (preferencesManager.getWindAlertEnabled() && weather.windSpeed > preferencesManager.getWindThreshold()) {
            sendWindNotification(notificationManager, weather.cityName, weather.windSpeed)
        }

        return Result.success()
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Weather Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for weather alerts"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendRainNotification(notificationManager: NotificationManager, cityName: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Rain Alert")
            .setContentText("It's raining in $cityName!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(RAIN_NOTIFICATION_ID, notification)
    }

    private fun sendWindNotification(
        notificationManager: NotificationManager,
        cityName: String,
        windSpeed: Float
    ) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Wind Alert")
            .setContentText("Wind speed in $cityName is ${windSpeed.toInt()} m/s!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(WIND_NOTIFICATION_ID, notification)
    }
}