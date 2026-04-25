package com.example.testapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.testapp.data.local.entities.ColorEntity

@Dao
interface ColorDao {

    @Insert
    suspend fun insertColor(color: ColorEntity): Long

    @Query("SELECT * FROM colors WHERE isStandalone = 1 AND paletteId IS NULL ORDER BY id DESC")
    suspend fun getStandaloneColors(): List<ColorEntity>

    @Query("SELECT * FROM colors WHERE paletteId = :paletteId ORDER BY id ASC")
    suspend fun getColorsForPalette(paletteId: Long): List<ColorEntity>

    @Delete
    suspend fun deleteColor(color: ColorEntity)

    @Query("DELETE FROM colors WHERE id = :colorId AND isStandalone = 1")
    suspend fun deleteStandaloneColorById(colorId: Long)

    @Query("DELETE FROM colors WHERE paletteId = :paletteId")
    suspend fun deleteColorsByPaletteId(paletteId: Long)
}