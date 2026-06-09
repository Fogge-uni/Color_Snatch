package com.example.testapp

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.compose.AppTheme
import com.example.testapp.presentation.AppNavigation
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var appContainer: AppContainer

    override fun attachBaseContext(newBase: Context?) {
        val prefs = newBase?.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val lang = prefs?.getString("language", "en") ?: "en"
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration(newBase?.resources?.configuration)
        config.setLocale(locale)
        val localizedContext = newBase?.createConfigurationContext(config)
        super.attachBaseContext(localizedContext ?: newBase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val themeMode = prefs.getString("theme_mode", "system") ?: "system"
        val darkTheme: Boolean? = when (themeMode) {
            "dark" -> true
            "light" -> false
            else -> null
        }

        appContainer = AppContainer(this)

        setContent {
            AppTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(appContainer)
                }
            }
        }
    }
}
