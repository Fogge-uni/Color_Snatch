package com.example.testapp.domain.usecases

import com.example.testapp.domain.model.Palette
import com.example.testapp.domain.repository.ColorRepository

class GetPaletteUseCase(
    private val repository: ColorRepository
) {
    suspend operator fun invoke(): List<Palette> = repository.getAllPalettes()
}