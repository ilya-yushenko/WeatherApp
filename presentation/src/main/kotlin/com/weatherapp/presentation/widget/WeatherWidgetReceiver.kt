package com.weatherapp.presentation.widget

import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

class WeatherWidgetReceiver : GlanceAppWidgetReceiver() {

    private lateinit var widget: WeatherWidget

    override val glanceAppWidget: GlanceAppWidget
        get() = widget

    override fun onReceive(context: Context, intent: Intent) {
        widget = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WeatherWidgetEntryPoint::class.java
        ).weatherWidget()
        super.onReceive(context, intent)
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        val updateRequest = OneTimeWorkRequestBuilder<WeatherWidgetUpdateWorker>().build()
        WorkManager.getInstance(context).enqueue(updateRequest)
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WeatherWidgetEntryPoint {
        fun weatherWidget(): WeatherWidget
    }
}