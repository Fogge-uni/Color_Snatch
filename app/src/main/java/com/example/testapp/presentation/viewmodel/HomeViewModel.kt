package com.example.testapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.domain.model.Color
import com.example.testapp.domain.model.Palette
import com.example.testapp.domain.usecases.DeleteColorUseCase
import com.example.testapp.domain.usecases.DeletePaletteUseCase
import com.example.testapp.domain.usecases.GetColorUseCase
import com.example.testapp.domain.usecases.GetPaletteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getPalettesUseCase: GetPaletteUseCase,
    private val getColorsUseCase: GetColorUseCase,
    private val deleteColorUseCase: DeleteColorUseCase,
    private val deletePaletteUseCase: DeletePaletteUseCase
) : ViewModel() {

    val palettes: StateFlow<List<Palette>> = getPalettesUseCase()
        .catch { e ->
            _error.value = "Не удалось загрузить палитры: ${e.message}"
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val colors: StateFlow<List<Color>> = getColorsUseCase()
        .catch { e ->
            _error.value = "Не удалось загрузить цвета: ${e.message}"
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun clearError() {
        _error.value = null
    }

    fun deleteColor(colorId: Long) {
        viewModelScope.launch {
            try {
                deleteColorUseCase(colorId)
            } catch (e: Exception) {
                _error.value = "Не удалось удалить цвет: ${e.message}"
            }
        }
    }

    fun deletePalette(paletteId: Long) {
        viewModelScope.launch {
            try {
                deletePaletteUseCase(paletteId)
            } catch (e: Exception) {
                _error.value = "Не удалось удалить палитру: ${e.message}"
            }
        }
    }
}