package com.example.testapp.domain.usecases

import com.example.testapp.domain.model.Color
import com.example.testapp.domain.repository.ColorRepository
import kotlinx.coroutines.flow.Flow

class GetColorUseCase(
    private val repository: ColorRepository
) {
    operator fun invoke(): Flow<List<Color>> = repository.getStandaloneColors()
}