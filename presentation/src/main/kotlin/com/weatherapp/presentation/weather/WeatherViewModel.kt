package com.weatherapp.presentation.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weatherapp.domain.usecase.GetWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

interface WeatherStateManager {
    val state: StateFlow<WeatherState>
    val effect: SharedFlow<WeatherEffect>
    fun onIntent(intent: WeatherIntent)
}

@HiltViewModel
open class WeatherViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
) : ViewModel(), WeatherStateManager {
    private val _state = MutableStateFlow(WeatherState())
    override val state: StateFlow<WeatherState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<WeatherEffect>()
    override val effect: SharedFlow<WeatherEffect> = _effect.asSharedFlow()

    override fun onIntent(intent: WeatherIntent) {
        when (intent) {
            is WeatherIntent.LoadWeather -> loadWeather(intent.city)
        }
    }

    private fun loadWeather(city: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val weather = getWeatherUseCase(city)
                _state.update { it.copy(isLoading = false, weather = weather) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
                _effect.emit(WeatherEffect.ShowError(e.message ?: "Error"))
            }
        }
    }
}