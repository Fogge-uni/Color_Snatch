package com.example.testapp.domain.usecases

import com.example.testapp.domain.repository.ColorRepository

class DeleteColorUseCase(
    private val repository: ColorRepository
) {
    suspend operator fun invoke(colorId: Long) {
        repository.deleteStandaloneColor(colorId)
    }
}