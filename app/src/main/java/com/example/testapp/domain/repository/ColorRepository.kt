package com.example.testapp.domain.repository

import com.example.testapp.domain.model.Color
import com.example.testapp.domain.model.Palette

interface ColorRepository {
    suspend fun saveStandaloneColor(color: Color): Long
    suspend fun savePalette(palette: Palette): Long
    suspend fun getStandaloneColors(): List<Color>
    suspend fun getAllPalettes(): List<Palette>
    suspend fun deleteStandaloneColor(colorId: Long)
    suspend fun deletePalette(paletteId: Long)
}