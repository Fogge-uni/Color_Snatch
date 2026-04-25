package com.example.testapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.testapp.domain.usecases.AddColorUseCase

class PickColorFactory(
    private val addColorUseCase: AddColorUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PickColorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PickColorViewModel(addColorUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}