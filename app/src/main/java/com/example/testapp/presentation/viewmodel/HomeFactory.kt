package com.example.testapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.testapp.domain.usecases.DeleteColorUseCase
import com.example.testapp.domain.usecases.DeletePaletteUseCase
import com.example.testapp.domain.usecases.GetColorUseCase
import com.example.testapp.domain.usecases.GetPaletteUseCase

class HomeFactory(
    private val getPalettesUseCase: GetPaletteUseCase,
    private val getColorsUseCase: GetColorUseCase,
    private val deleteColorUseCase: DeleteColorUseCase,
    private val deletePaletteUseCase: DeletePaletteUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(
                getPalettesUseCase,
                getColorsUseCase,
                deleteColorUseCase,
                deletePaletteUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}