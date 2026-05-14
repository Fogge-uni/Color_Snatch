package com.example.testapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.testapp.data.local.entities.PaletteEntity
import com.example.testapp.data.local.entities.PaletteWithColors
import kotlinx.coroutines.flow.Flow

@Dao
interface PaletteDao {

    @Insert
    suspend fun insertPalette(palette: PaletteEntity): Long

    @Query("SELECT * FROM palettes ORDER BY id DESC")
    suspend fun getAllPalettes(): List<PaletteEntity>

    @Transaction
    @Query("SELECT * FROM palettes ORDER BY id DESC")
    fun getAllPalettesWithColors(): Flow<List<PaletteWithColors>>

    @Delete
    suspend fun deletePalette(palette: PaletteEntity)

    @Query("SELECT * FROM palettes WHERE id = :paletteId LIMIT 1")
    suspend fun getPaletteById(paletteId: Long): PaletteEntity?
}