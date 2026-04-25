package com.example.testapp.presentation.ui.screens

import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.compose.onPrimaryContainerDark
import com.example.compose.primaryContainerDark
import com.example.testapp.presentation.Screen
import com.example.testapp.presentation.viewmodel.PickColorViewModel
import com.example.testapp.utils.buildColorCodes
import com.example.testapp.utils.copyToClipboard
import com.example.testapp.utils.deletePhotoFile
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.ImageColorPicker
import com.github.skydoves.colorpicker.compose.PaletteContentScale
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import kotlinx.coroutines.launch

@Composable
fun PickColorScreen(
    photoPath: String,
    navController: NavController,
    viewModel: PickColorViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val bitmap by remember(photoPath) {
        mutableStateOf(BitmapFactory.decodeFile(photoPath))
    }

    val controller = rememberColorPickerController()

    var selectedHex by remember { mutableStateOf<String?>(null) }
    var selectedColor by remember { mutableStateOf(Color.Transparent) }
    var colorCodes by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }

    LaunchedEffect(bitmap) {
        bitmap?.let { bmp ->
            controller.setPaletteImageBitmap(bmp.asImageBitmap())
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            deletePhotoFile(photoPath)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
                ImageColorPicker(
                    modifier = Modifier.fillMaxSize(),
                    controller = controller,
                    paletteContentScale = PaletteContentScale.CROP,
                    paletteImageBitmap = bitmap.asImageBitmap(),
                    onColorChanged = { colorEnvelope: ColorEnvelope ->
                        selectedColor = colorEnvelope.color
                        val hex = colorEnvelope.hexCode
                        selectedHex = if (hex.startsWith("#")) hex else "#$hex"
                        colorCodes = buildColorCodes(colorEnvelope.color.toArgb())
                    }
                )
        }
        if (selectedHex != null) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                AlphaTile(
                    modifier = Modifier.fillMaxWidth().height(60.dp).padding(horizontal = 8.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    controller = controller
                )

                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(colorCodes) { (name, value) ->
                        Row(modifier = Modifier.fillMaxWidth().clickable {
                            copyToClipboard(context, value)
                            Toast.makeText(context, "Скопировано: $value", Toast.LENGTH_SHORT).show()
                        }) {
                            Text(
                                text = "$name: ",
                                fontSize = 18.sp
                            )
                            Text(
                                text = value,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
                Box(modifier = Modifier.fillMaxWidth().height(70.dp), contentAlignment = Alignment.BottomCenter) {

                    Button(
                        onClick = {
                            val hex = selectedHex ?: return@Button
                            scope.launch {
                                viewModel.saveColor(hex, photoPath)
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Home.route) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryContainerDark,
                            contentColor = onPrimaryContainerDark
                        ),
                        modifier = Modifier.width(160.dp).height(70.dp).padding(8.dp),
                    ) {
                        Text("Save color", style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }
    }
}