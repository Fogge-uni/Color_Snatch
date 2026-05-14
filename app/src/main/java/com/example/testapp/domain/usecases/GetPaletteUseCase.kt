package com.example.testapp.domain.usecases

import com.example.testapp.domain.model.Palette
import com.example.testapp.domain.repository.ColorRepository
import kotlinx.coroutines.flow.Flow

class GetPaletteUseCase(
    private val repository: ColorRepository
) {
    operator fun invoke(): Flow<List<Palette>> = repository.getAllPalettes()
}