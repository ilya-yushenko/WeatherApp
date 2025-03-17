package com.weatherapp.presentation.widget

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.action
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.weatherapp.core.AppPreferencesManager
import com.weatherapp.core.model.WidgetWeatherData
import javax.inject.Inject

class WeatherWidget @Inject constructor(
    private val preferencesManager: AppPreferencesManager
) : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val weather = preferencesManager.getWidgetWeather()
        val bitmap = preferencesManager.getWidgetBitmap()

        provideContent {
            WeatherWidgetContent(context, weather, bitmap)
        }
    }

    @Composable
    private fun WeatherWidgetContent(
        context: Context,
        weather: WidgetWeatherData?,
        bitmap: Bitmap?
    ) {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val backgroundColorProvider = ColorProvider(Color(0xff6b9cff), Color(0xff6b9cff))
        val whiteColorProvider = ColorProvider(Color(0xffffffff), Color(0xffffffff))

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(backgroundColorProvider)
                .padding(8.dp)
                .clickable(action { pendingIntent.send() }),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (weather != null) {
                Spacer(GlanceModifier.defaultWeight())
                Text(
                    text = weather.cityName,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = whiteColorProvider
                    )
                )
                Spacer(GlanceModifier.size(4.dp))
                Text(
                    text = "${weather.temperature.toInt()}°C",
                    style = TextStyle(
                        fontSize = 24.sp,
                        color = whiteColorProvider
                    )
                )
                Spacer(GlanceModifier.size(4.dp))
                if (bitmap != null) {
                    Image(
                        provider = ImageProvider(bitmap),
                        contentDescription = "Weather icon",
                        modifier = GlanceModifier.size(64.dp)
                    )
                }
                Spacer(GlanceModifier.size(4.dp))
                Text(
                    text = weather.description.replaceFirstChar { it.uppercase() },
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = whiteColorProvider
                    )
                )
                Spacer(GlanceModifier.defaultWeight())
            } else {
                Text(
                    text = "No data available",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = whiteColorProvider
                    )
                )
            }
        }
    }
}