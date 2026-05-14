package com.example.testapp.presentation.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.compose.onPrimaryContainerDark
import com.example.compose.primaryContainerDark
import com.example.compose.secondaryContainerDark
import com.example.testapp.presentation.Screen
import com.example.testapp.presentation.viewmodel.PickPaletteViewModel
import com.example.testapp.utils.deletePhotoFile
import com.example.testapp.utils.getColorAtPosition
import com.example.testapp.utils.isDarkColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt


@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun PickPaletteScreen(
    photoPath: String,
    navController: NavController,
    viewModel: PickPaletteViewModel
) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(photoPath) {
        bitmap = withContext(Dispatchers.IO) {
            BitmapFactory.decodeFile(photoPath)
        }
    }
    DisposableEffect(photoPath) {
        onDispose {
            deletePhotoFile(photoPath)
            bitmap?.recycle()
        }
    }

    val saved by viewModel.saved.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(saved) {
        if (saved) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Home.route) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    val pickerPositions = remember { mutableStateListOf<Offset>() }
    var activePickerIndex by remember { mutableIntStateOf(0) }
    var showNameDialog by remember { mutableStateOf(false) }
    var paletteName by remember { mutableStateOf("") }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    if (bitmap == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LaunchedEffect(bitmap!!) {
        if (bitmap != null && pickerPositions.isEmpty()) {
            val width = bitmap!!.width.toFloat()
            val height = bitmap!!.height.toFloat()
            pickerPositions.addAll(
                listOf(
                    Offset(width * 0.3f, height * 0.5f),
                    Offset(width * 0.4f, height * 0.5f),
                    Offset(width * 0.5f, height * 0.5f),
                    Offset(width * 0.6f, height * 0.5f),
                    Offset(width * 0.7f, height * 0.5f)
                )
            )
            activePickerIndex = 0
        }
    }

    val selectedColors = remember(bitmap, pickerPositions.toList()) {
        pickerPositions.map { offset ->
            getColorAtPosition(bitmap, offset.x.toInt(), offset.y.toInt()) ?: "#FFFFFF"
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { layoutCoordinates ->
                        containerSize = layoutCoordinates.size
                    }
            ) {
                bitmap?.let { bmp ->

                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(bmp) {
                                awaitPointerEventScope {
                                    while (true) {

                                        val down = awaitFirstDown(requireUnconsumed = false)
                                        val downPos = down.position


                                        val bitmapDownOffset = mapDisplayOffsetToBitmapOffset(
                                            tapOffset = downPos,
                                            displayWidth = size.width.toFloat(),
                                            displayHeight = size.height.toFloat(),
                                            bitmapWidth = bmp.width.toFloat(),
                                            bitmapHeight = bmp.height.toFloat(),
                                            contentScale = ContentScale.Fit
                                        )


                                        val density = 24.dp.toPx()
                                        var nearestIndex = -1
                                        pickerPositions.forEachIndexed { index, bitmapPos ->
                                            val displayPos = mapBitmapOffsetToDisplayOffset(
                                                bitmapOffset = bitmapPos,
                                                displayWidth = size.width.toFloat(),
                                                displayHeight = size.height.toFloat(),
                                                bitmapWidth = bmp.width.toFloat(),
                                                bitmapHeight = bmp.height.toFloat(),
                                                contentScale = ContentScale.Fit
                                            )
                                            if ((displayPos - downPos).getDistance() < density) {
                                                nearestIndex = index
                                            }
                                        }

                                        if (nearestIndex != -1) {

                                            activePickerIndex = nearestIndex
                                            val draggedIndex = nearestIndex

                                            val pointerId = down.id
                                            while (true) {
                                                val event = awaitPointerEvent()
                                                val pointer = event.changes
                                                    .firstOrNull { it.id == pointerId }
                                                    ?: break

                                                if (pointer.pressed) {
                                                    val newBitmapOffset = mapDisplayOffsetToBitmapOffset(
                                                        tapOffset = pointer.position,
                                                        displayWidth = size.width.toFloat(),
                                                        displayHeight = size.height.toFloat(),
                                                        bitmapWidth = bmp.width.toFloat(),
                                                        bitmapHeight = bmp.height.toFloat(),
                                                        contentScale = ContentScale.Fit
                                                    )
                                                    pickerPositions[draggedIndex] = newBitmapOffset
                                                    pointer.consume()
                                                } else {
                                                    pointer.consume()
                                                    break
                                                }
                                            }
                                        } else {

                                            if (activePickerIndex in pickerPositions.indices) {
                                                pickerPositions[activePickerIndex] = bitmapDownOffset
                                            }
                                        }
                                    }
                                }
                            }
                    )

                    pickerPositions.forEachIndexed { index, bitmapOffset ->
                        val displayOffset = mapBitmapOffsetToDisplayOffset(
                            bitmapOffset = bitmapOffset,
                            displayWidth = containerSize.width.toFloat(),
                            displayHeight = containerSize.height.toFloat(),
                            bitmapWidth = bmp.width.toFloat(),
                            bitmapHeight = bmp.height.toFloat(),
                            contentScale = ContentScale.Fit
                        )

                        Box(
                            modifier = Modifier
                                .offset {
                                    IntOffset(
                                        (displayOffset.x - 12.dp.toPx()).roundToInt(),
                                        (displayOffset.y - 12.dp.toPx()).roundToInt()
                                    )
                                }
                                .size(24.dp)
                                .background(
                                    color = if (index == activePickerIndex) secondaryContainerDark else Color.White,
                                    shape = CircleShape
                                )
                                .border(1.dp, secondaryContainerDark, CircleShape)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (bitmap != null && pickerPositions.size < 8) {
                                val newOffset = Offset(
                                    bitmap!!.width * 0.5f,
                                    bitmap!!.height * 0.5f
                                )
                                pickerPositions.add(newOffset)
                                activePickerIndex = pickerPositions.lastIndex
                            }
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Добавить цвет", tint = Color.White)
                    }
                    IconButton(
                        onClick = {
                            if (pickerPositions.size > 1) {
                                pickerPositions.removeAt(activePickerIndex)
                                if (activePickerIndex >= pickerPositions.size) {
                                    activePickerIndex = pickerPositions.lastIndex
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Удалить цвет", tint = Color.White)
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .height(250.dp)
                .fillMaxWidth()
        ) {
            selectedColors.forEachIndexed { index, hex ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .background(Color(android.graphics.Color.parseColor(hex)))
                        .border(
                            width = if (index == activePickerIndex) 3.dp else 0.dp,
                            color = if (index == activePickerIndex) Color.White else Color.Transparent
                        )
                        .clickable { activePickerIndex = index },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = hex,
                        color = if (isDarkColor(hex)) Color.White else Color.Black,
                        fontSize = 10.sp
                    )
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth().height(70.dp), contentAlignment = Alignment.BottomCenter) {
            Button(
                onClick = { showNameDialog = true },
                modifier = Modifier
                    .width(200.dp)
                    .padding(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryContainerDark,
                    contentColor = onPrimaryContainerDark
                )
            ) {
                Text("Save palette", style = MaterialTheme.typography.titleLarge)
            }
        }
    }

    if (showNameDialog) {
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            title = { Text("Name your palette") },
            text = {
                OutlinedTextField(
                    value = paletteName,
                    onValueChange = { paletteName = it },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (paletteName.isNotBlank() && selectedColors.isNotEmpty()) {
                            viewModel.savePalette(name = paletteName.trim(),
                                hexColors = selectedColors) }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryContainerDark,
                        contentColor = onPrimaryContainerDark
                    )
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showNameDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryContainerDark,
                        contentColor = onPrimaryContainerDark
                    )
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

private fun mapDisplayOffsetToBitmapOffset(
    tapOffset: Offset,
    displayWidth: Float,
    displayHeight: Float,
    bitmapWidth: Float,
    bitmapHeight: Float,
    contentScale: ContentScale
): Offset {
    val scale = if (contentScale == ContentScale.Fit) {
        minOf(displayWidth / bitmapWidth, displayHeight / bitmapHeight)
    } else {
        maxOf(displayWidth / bitmapWidth, displayHeight / bitmapHeight)
    }

    val renderedWidth = bitmapWidth * scale
    val renderedHeight = bitmapHeight * scale
    val left = (displayWidth - renderedWidth) / 2f
    val top = (displayHeight - renderedHeight) / 2f
    val x = ((tapOffset.x - left) / scale).coerceIn(0f, bitmapWidth - 1)
    val y = ((tapOffset.y - top) / scale).coerceIn(0f, bitmapHeight - 1)

    return Offset(x, y)
}

private fun mapBitmapOffsetToDisplayOffset(
    bitmapOffset: Offset,
    displayWidth: Float,
    displayHeight: Float,
    bitmapWidth: Float,
    bitmapHeight: Float,
    contentScale: ContentScale
): Offset {
    val scale = if (contentScale == ContentScale.Fit) {
        minOf(displayWidth / bitmapWidth, displayHeight / bitmapHeight)
    } else {
        maxOf(displayWidth / bitmapWidth, displayHeight / bitmapHeight)
    }

    val renderedWidth = bitmapWidth * scale
    val renderedHeight = bitmapHeight * scale
    val left = (displayWidth - renderedWidth) / 2f
    val top = (displayHeight - renderedHeight) / 2f

    return Offset(
        x = left + bitmapOffset.x * scale,
        y = top + bitmapOffset.y * scale
    )
}