package com.example.testapp.data.repository

import com.example.testapp.data.local.entities.AppDatabase
import com.example.testapp.data.local.entities.ColorEntity
import com.example.testapp.data.local.entities.PaletteEntity
import com.example.testapp.domain.model.Color
import com.example.testapp.domain.model.Palette
import com.example.testapp.domain.repository.ColorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ColorRepositoryImpl(
    private val database: AppDatabase
) : ColorRepository {

    override suspend fun saveStandaloneColor(color: Color): Long {
        val entity = ColorEntity(
            hexCode = color.hex,
            isStandalone = true,
            paletteId = null
        )
        return database.colorDao().insertColor(entity)
    }

    override suspend fun savePalette(palette: Palette): Long {
        val paletteEntity = PaletteEntity(
            name = palette.name
        )
        val paletteId = database.paletteDao().insertPalette(paletteEntity)

        palette.colors.forEach { color ->
            val colorEntity = ColorEntity(
                hexCode = color.hex,
                paletteId = paletteId,
                isStandalone = false
            )
            database.colorDao().insertColor(colorEntity)
        }

        return paletteId
    }

    override fun getStandaloneColors(): Flow<List<Color>> {
        return database.colorDao().getStandaloneColors().map { entities ->
            entities.map {entity -> Color(id = entity.id, hex = entity.hexCode) }

        }
    }

    override fun getAllPalettes(): Flow<List<Palette>> {
        return database.paletteDao().getAllPalettesWithColors().map { list ->
            list.map { pwc -> Palette(
                id = pwc.palette.id,
                name = pwc.palette.name,
                colors = pwc.colors.map { Color(id = it.id, hex = it.hexCode) }
                )
            }
        }
    }

    override suspend fun deleteStandaloneColor(colorId: Long) {
        database.colorDao().deleteStandaloneColorById(colorId)
    }

    override suspend fun deletePalette(paletteId: Long) {
        val palette = database.paletteDao().getPaletteById(paletteId)
        if (palette != null) {
            database.colorDao().deleteColorsByPaletteId(paletteId)
            database.paletteDao().deletePalette(palette)
        }
    }
}