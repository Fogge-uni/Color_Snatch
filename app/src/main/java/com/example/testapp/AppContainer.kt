package com.example.testapp

import android.content.Context
import com.example.testapp.data.local.entities.AppDatabase
import com.example.testapp.data.repository.ColorRepositoryImpl
import com.example.testapp.domain.repository.ColorRepository
import com.example.testapp.domain.usecases.*

class AppContainer(private val context: Context) {

    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(context)
    }

    private val colorRepository: ColorRepository by lazy {
        ColorRepositoryImpl(database)
    }

    val addStandaloneColorUseCase: AddColorUseCase by lazy {
        AddColorUseCase(colorRepository)
    }

    val addPaletteUseCase: AddPaletteUseCase by lazy {
        AddPaletteUseCase(colorRepository)
    }

    val getStandaloneColorsUseCase: GetColorUseCase by lazy {
        GetColorUseCase(colorRepository)
    }

    val getAllPalettesUseCase: GetPaletteUseCase by lazy {
        GetPaletteUseCase(colorRepository)
    }

    val deleteColorUseCase: DeleteColorUseCase by lazy {
        DeleteColorUseCase(colorRepository)
    }

    val deletePaletteUseCase: DeletePaletteUseCase by lazy {
        DeletePaletteUseCase(colorRepository)
    }
}