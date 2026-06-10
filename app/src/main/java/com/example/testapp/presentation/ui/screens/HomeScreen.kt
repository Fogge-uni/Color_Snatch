package com.example.testapp.presentation.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.example.compose.onPrimaryContainerDark
import com.example.compose.primaryContainerDark
import com.example.testapp.domain.model.Color
import com.example.testapp.domain.model.Palette
import com.example.testapp.presentation.Screen
import com.example.testapp.presentation.viewmodel.HomeViewModel
import com.example.testapp.utils.buildColorCodes
import com.example.testapp.utils.copyToClipboard
import androidx.compose.ui.graphics.Color as ComposeColor
import com.example.testapp.R
import com.example.compose.primaryLight
import com.example.compose.onPrimaryLight
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel
) {
    val palettes by viewModel.palettes.collectAsState()
    val colors by viewModel.colors.collectAsState()
    val error by viewModel.error.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    val context = LocalContext.current
    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            Column {
                Surface(
                    color = primaryLight,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Color Snatch",
                            fontFamily = FontFamily(Font(R.font.lobster_regular)),
                            fontSize = 30.sp,
                            color = onPrimaryLight,
                        )
                        IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = stringResource(R.string.settings),
                                tint = onPrimaryLight
                            )
                        }
                    }
                }

                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                stringResource(R.string.tab_colors),
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                stringResource(R.string.tab_palettes),
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.background
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            val mode = if (selectedTab == 0) "color" else "palette"
                            navController.navigate(Screen.Camera.createRoute(mode))
                        },
                        modifier = Modifier
                            .height(54.dp)
                            .width(240.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryContainerDark,
                            contentColor = onPrimaryContainerDark
                        ),
                    ) {
                        Text(
                            text = if (selectedTab == 0) stringResource(R.string.snatch_color)
                            else stringResource(R.string.create_palette),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
        }
    ) { padding ->
        when {
            selectedTab == 0 -> {
                ColorListScreen(
                    colors = colors,
                    viewModel = viewModel,
                    modifier = Modifier.padding(padding)
                )
            }
            else -> {
                PaletteListScreen(
                    palettes = palettes,
                    viewModel = viewModel,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
fun ColorListScreen(
    colors: List<Color>,
    viewModel: HomeViewModel,
    modifier: Modifier
) {
    var selectedColorForDetail by remember { mutableStateOf<Color?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        if (colors.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.no_colors_saved),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                items(colors, key = { it.id }) { color ->
                    ColorCard(
                        color = color,
                        viewModel = viewModel,
                        onClick = { selectedColorForDetail = color }
                    )
                }
            }

            selectedColorForDetail?.let { color ->
                ColorDetailDialog(
                    color = color,
                    onDismiss = { selectedColorForDetail = null }
                )
            }
        }
    }
}

@Composable
fun ColorCard(
    color: Color,
    viewModel: HomeViewModel,
    onClick: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val colorInt = remember(color.hex) { color.hex.toColorInt() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ComposeColor(colorInt))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(color.hex,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer)
                    Text(
                        "RGB: ${android.graphics.Color.red(colorInt)}, ${android.graphics.Color.green(colorInt)}, ${android.graphics.Color.blue(colorInt)}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_color_title)) },
            text = { Text(stringResource(R.string.delete_color_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteColor(color.id)
                        showDeleteDialog = false
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = primaryContainerDark,
                        contentColor = onPrimaryContainerDark
                    )
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false },
                    colors = ButtonDefaults.buttonColors(
                    containerColor = primaryContainerDark,
                    contentColor = onPrimaryContainerDark
                )) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun PaletteListScreen(
    palettes: List<Palette>,
    viewModel: HomeViewModel,
    modifier: Modifier
) {
    var selectedPalette by remember { mutableStateOf<Palette?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        if (palettes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.no_palettes_saved),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                items(palettes, key = { it.id }) { palette ->
                    PaletteCard(
                        palette = palette,
                        viewModel = viewModel,
                        onClick = { selectedPalette = palette }
                    )
                }
            }

            selectedPalette?.let { palette ->
                PaletteDetailDialog(
                    palette = palette,
                    onDismiss = { selectedPalette = null }
                )
            }
        }
    }
}

@Composable
fun PaletteCard(
    palette: Palette,
    viewModel: HomeViewModel,
    onClick: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = palette.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                palette.colors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .background(ComposeColor(color.hex.toColorInt()))
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_palette_title)) },
            text = { Text(stringResource(R.string.delete_palette_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deletePalette(palette.id)
                        showDeleteDialog = false
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = primaryContainerDark,
                        contentColor = onPrimaryContainerDark
                    )
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }, colors = ButtonDefaults.buttonColors(
                    containerColor = primaryContainerDark,
                    contentColor = onPrimaryContainerDark
                )) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun PaletteDetailDialog(
    palette: Palette,
    onDismiss: () -> Unit
) {
    var selectedColorForDetail by remember { mutableStateOf<Color?>(null) }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = palette.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp, max = 400.dp)
                ) {
                    items(palette.colors) { color ->
                        val colorInt = color.hex.toColorInt()
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(ComposeColor(colorInt))
                                .clickable { selectedColorForDetail = color }
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {

                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(
                        containerColor = primaryContainerDark,
                        contentColor = onPrimaryContainerDark
                    )) {
                        Text(stringResource(R.string.close))
                    }
                }
            }
        }
    }

    selectedColorForDetail?.let { color ->
        ColorDetailDialog(
            color = color,
            onDismiss = { selectedColorForDetail = null }
        )
    }
}

@Composable
fun ColorDetailDialog(
    color: Color,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val colorInt = color.hex.toColorInt()
    val colorCodes = remember(colorInt) { buildColorCodes(colorInt) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ComposeColor(colorInt))
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                colorCodes.forEach { (name, value) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                copyToClipboard(context, value)
                                Toast.makeText(context, "Скопировано: $value", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "$name:", fontWeight = FontWeight.Bold)
                        Text(text = value, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(
                containerColor = primaryContainerDark,
                contentColor = onPrimaryContainerDark
            )) {
                Text(stringResource(R.string.close))
            }
        }
    )
}