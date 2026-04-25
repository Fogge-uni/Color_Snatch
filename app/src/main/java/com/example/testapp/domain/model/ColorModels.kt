package com.example.testapp.domain.model

data class Color(
    val id: Long,
    val hex: String,
)
data class Palette(
    val id: Long,
    val name: String,
    val colors: List<Color>,
)
