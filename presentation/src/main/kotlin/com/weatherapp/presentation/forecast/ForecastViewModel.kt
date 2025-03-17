package com.weatherapp.presentation.forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weatherapp.domain.usecase.GetForecastUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

interface ForecastStateManager {
    val state: StateFlow<ForecastState>
    val effect: SharedFlow<ForecastEffect>
    fun onIntent(intent: ForecastIntent)
}

@HiltViewModel
class ForecastViewModel @Inject constructor(
    private val getForecastUseCase: GetForecastUseCase
) : ViewModel(), ForecastStateManager {
    private val _state = MutableStateFlow(ForecastState())
    override val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ForecastEffect>()
    override val effect = _effect.asSharedFlow()

    override fun onIntent(intent: ForecastIntent) {
        when (intent) {
            is ForecastIntent.LoadForecast -> loadForecast(intent.city)
        }
    }

    private fun loadForecast(city: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val forecast = getForecastUseCase(city)
                _state.value = _state.value.copy(isLoading = false, forecast = forecast)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
                _effect.emit(ForecastEffect.ShowError(e.message ?: "Unknown error"))
            }
        }
    }
}