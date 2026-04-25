package com.example.testapp.domain.usecases

import com.example.testapp.domain.model.Color
import com.example.testapp.domain.model.Palette
import com.example.testapp.domain.repository.ColorRepository

class AddPaletteUseCase(
    private val repository: ColorRepository
) {
    suspend operator fun invoke(name: String, hexColors: List<String>): Long {
        val colors = hexColors.map { hex ->
            Color(
                id = 0,
                hex = hex,

            )
        }

        val palette = Palette(
            id = 0,
            name = name,
            colors = colors
        )
        return repository.savePalette(palette)
    }
}