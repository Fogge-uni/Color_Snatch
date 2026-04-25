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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getPalettesUseCase: GetPaletteUseCase,
    private val getColorsUseCase: GetColorUseCase,
    private val deleteColorUseCase: DeleteColorUseCase,
    private val deletePaletteUseCase: DeletePaletteUseCase
) : ViewModel() {

    private val _palettes = MutableStateFlow<List<Palette>>(emptyList())
    val palettes = _palettes.asStateFlow()

    private val _colors = MutableStateFlow<List<Color>>(emptyList())
    val colors = _colors.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            _palettes.value = getPalettesUseCase()
            _colors.value = getColorsUseCase()
            _isLoading.value = false
        }
    }

    fun deleteColor(colorId: Long) {
        viewModelScope.launch {
            deleteColorUseCase(colorId)
            loadData()
        }
    }

    fun deletePalette(paletteId: Long) {
        viewModelScope.launch {
            deletePaletteUseCase(paletteId)
            loadData()
        }
    }
}