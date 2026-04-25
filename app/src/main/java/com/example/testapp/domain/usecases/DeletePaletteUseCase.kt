package com.example.testapp.domain.usecases

import com.example.testapp.domain.repository.ColorRepository

class DeletePaletteUseCase(
    private val repository: ColorRepository
) {
    suspend operator fun invoke(paletteId: Long) {
        repository.deletePalette(paletteId)
    }
}