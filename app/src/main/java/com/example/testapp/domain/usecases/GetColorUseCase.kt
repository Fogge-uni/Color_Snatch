package com.example.testapp.domain.usecases

import com.example.testapp.domain.model.Color
import com.example.testapp.domain.repository.ColorRepository

class GetColorUseCase(
    private val repository: ColorRepository
) {
    suspend operator fun invoke(): List<Color> = repository.getStandaloneColors()
}