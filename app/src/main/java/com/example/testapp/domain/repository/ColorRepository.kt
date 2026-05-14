package com.example.testapp.domain.repository

import com.example.testapp.domain.model.Color
import com.example.testapp.domain.model.Palette
import kotlinx.coroutines.flow.Flow

interface ColorRepository {
    suspend fun saveStandaloneColor(color: Color): Long
    suspend fun savePalette(palette: Palette): Long
    fun getStandaloneColors(): Flow<List<Color>>
    fun getAllPalettes(): Flow<List<Palette>>
    suspend fun deleteStandaloneColor(colorId: Long)
    suspend fun deletePalette(paletteId: Long)
}