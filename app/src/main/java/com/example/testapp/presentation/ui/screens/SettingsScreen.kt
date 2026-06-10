package com.example.testapp.presentation.ui.screens

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.testapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    val currentLang = prefs.getString("language", "en") ?: "en"
    val currentTheme = prefs.getString("theme_mode", "system") ?: "system"

    var selectedLang by remember { mutableStateOf(currentLang) }
    var selectedTheme by remember { mutableStateOf(currentTheme) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back_to_home)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                text = stringResource(R.string.language),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )

            listOf(
                "en" to stringResource(R.string.english),
                "ru" to stringResource(R.string.russian)
            ).forEach { (code, name) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedLang = code }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedLang == code,
                        onClick = { selectedLang = code },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.tertiary,
                            unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = name, style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.theme),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )

            listOf(
                "system" to stringResource(R.string.system_theme),
                "light" to stringResource(R.string.light_theme),
                "dark" to stringResource(R.string.dark_theme)
            ).forEach { (code, name) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedTheme = code }
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedTheme == code,
                        onClick = { selectedTheme = code },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.tertiary,
                            unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = name, style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        prefs.edit()
                            .putString("language", selectedLang)
                            .putString("theme_mode", selectedTheme)
                            .apply()
                        (context as? Activity)?.recreate()
                    },
                    modifier = Modifier
                        .width(240.dp)
                        .height(56.dp)
                ) {
                    Text(stringResource(R.string.save_changes), style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}