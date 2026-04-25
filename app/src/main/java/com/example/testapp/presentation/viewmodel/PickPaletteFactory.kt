package com.example.testapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.testapp.domain.usecases.AddPaletteUseCase

class PickPaletteFactory(
    private val addPaletteUseCase: AddPaletteUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PickPaletteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PickPaletteViewModel(addPaletteUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}