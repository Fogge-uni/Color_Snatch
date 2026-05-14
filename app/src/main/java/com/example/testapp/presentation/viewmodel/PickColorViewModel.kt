package com.example.testapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.domain.usecases.AddColorUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PickColorViewModel(
    private val addColorUseCase: AddColorUseCase
) : ViewModel() {

     private val _saved = MutableStateFlow(false)
    val saved = _saved.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

        fun saveColor(hex: String) {
        viewModelScope.launch {
            try {
                addColorUseCase(hex)
                _saved.value = true
            } catch (e: Exception) {
                _error.value = "Не удалось сохранить цвет: ${e.message}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}