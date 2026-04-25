package com.example.testapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "colors")
data class ColorEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hexCode: String,
    val paletteId: Long? = null,
    val isStandalone: Boolean = false
)