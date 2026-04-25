package com.example.testapp.domain.usecases

import com.example.testapp.domain.model.Color
import com.example.testapp.domain.repository.ColorRepository

class AddColorUseCase(
    private val repository: ColorRepository
) {
    suspend operator fun invoke(hex: String): Long {
        val color = Color(
            id = 0,
            hex = hex,
        )
        return repository.saveStandaloneColor(color)
    }
}