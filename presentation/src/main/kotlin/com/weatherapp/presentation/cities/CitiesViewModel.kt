package com.weatherapp.presentation.cities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weatherapp.domain.usecase.GetCitySuggestionsUseCase
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

interface CitiesStateManager {
    val state: StateFlow<CitiesState>
    val effect: SharedFlow<CitiesEffect>
    fun onIntent(intent: CitiesIntent)
}

@HiltViewModel
class CitiesViewModel @Inject constructor(
    private val getCitySuggestionsUseCase: GetCitySuggestionsUseCase
) : ViewModel(), CitiesStateManager {

    private val _state = MutableStateFlow(CitiesState())
    override val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<CitiesEffect>()
    override val effect: SharedFlow<CitiesEffect> = _effect.asSharedFlow()

    override fun onIntent(intent: CitiesIntent) {
        when (intent) {
            is CitiesIntent.SearchCities -> searchCities(intent.query)
        }
    }

    private fun searchCities(query: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, suggestions = emptyList()) }
            try {
                val suggestions = getCitySuggestionsUseCase(query)
                _state.update { it.copy(isLoading = false, suggestions = suggestions) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
                _effect.emit(CitiesEffect.ShowError(e.message ?: "Failed to load suggestions"))
            }
        }
    }
}