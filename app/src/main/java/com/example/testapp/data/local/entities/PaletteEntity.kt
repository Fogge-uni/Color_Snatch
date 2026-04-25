package com.example.testapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "palettes")
data class PaletteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)