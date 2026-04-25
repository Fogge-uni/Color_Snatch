package com.example.testapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.testapp.domain.usecases.AddColorUseCase
import com.example.testapp.utils.deletePhotoFile

class PickColorViewModel(
    private val addColorUseCase: AddColorUseCase
) : ViewModel() {

    suspend fun saveColor(hex: String, photoPath: String) {
        addColorUseCase(hex)
        deletePhotoFile(photoPath)
    }
}