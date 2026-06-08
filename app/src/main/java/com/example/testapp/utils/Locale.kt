package com.example.testapp.utils


import android.content.Context
import android.content.res.Configuration
import java.util.Locale
object Locale {

    private const val PREFS_NAME = "app_settings"
    private const val KEY_LANGUAGE = "language"

    fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }

    fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, "en") ?: "en"
    }

    fun applySavedLocale(context: Context) {
        setLocale(context, getSavedLanguage(context))
    }
}