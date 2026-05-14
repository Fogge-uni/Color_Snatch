package com.example.testapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.domain.usecases.AddPaletteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PickPaletteViewModel(
    private val addPaletteUseCase: AddPaletteUseCase
) : ViewModel() {

    private val _saved = MutableStateFlow(false)
    val saved = _saved.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun savePalette(name: String, hexColors: List<String>) {
        viewModelScope.launch {
            try {
                addPaletteUseCase(name, hexColors)
                _saved.value = true
            } catch (e: Exception) {
                _error.value = "Не удалось сохранить палитру: ${e.message}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}