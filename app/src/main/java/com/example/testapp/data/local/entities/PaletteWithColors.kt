package com.example.testapp.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

data class PaletteWithColors(
    @Embedded val palette: PaletteEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "paletteId"
    )
    val colors: List<ColorEntity>
)