package com.weatherapp.presentation.widget

import android.content.Context
import androidx.core.graphics.drawable.toBitmap
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import coil.ImageLoader
import coil.request.ImageRequest
import com.weatherapp.core.AppPreferencesManager
import com.weatherapp.core.model.WidgetWeatherData
import com.weatherapp.domain.usecase.GetWidgetWeatherUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class WeatherWidgetUpdateWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val weatherWidget: WeatherWidget,
    private val getWidgetWeatherUseCase: GetWidgetWeatherUseCase,
    private val preferencesManager: AppPreferencesManager
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val city = preferencesManager.getCurrentCity() ?: return Result.success()
        return try {
            val weather = getWidgetWeatherUseCase(city)
            val widgetWeatherData = WidgetWeatherData(
                cityName = weather.cityName,
                temperature = weather.temperature,
                description = weather.description,
                icon = weather.icon
            )

            val imageLoader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data("https://openweathermap.org/img/wn/${weather.icon}@2x.png")
                .build()
            val bitmap = imageLoader.execute(request).drawable?.toBitmap()

            preferencesManager.saveWidgetWeather(widgetWeatherData, bitmap)

            weatherWidget.updateAll(context)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}