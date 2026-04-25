package com.example.testapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.testapp.domain.usecases.AddPaletteUseCase
import com.example.testapp.utils.deletePhotoFile

class PickPaletteViewModel(
    private val addPaletteUseCase: AddPaletteUseCase
) : ViewModel() {

    suspend fun savePalette(name: String, hexColors: List<String>, photoPath: String) {
        addPaletteUseCase(name, hexColors)
        deletePhotoFile(photoPath)
    }
}