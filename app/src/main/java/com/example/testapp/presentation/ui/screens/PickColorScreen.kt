package com.example.testapp.presentation.ui.screens

import android.graphics.Bitmap
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.testapp.R

@Composable
fun PickColorScreen(
    photoPath: String,
    navController: NavController,
    viewModel: PickColorViewModel
) {
    val context = LocalContext.current
    val controller = rememberColorPickerController()
    var selectedHex by remember { mutableStateOf<String?>(null) }
    var colorCodes by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
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

    LaunchedEffect(bitmap) {
        bitmap?.let { bmp -> controller.setPaletteImageBitmap(bmp.asImageBitmap()) }
    }

    if (bitmap == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            androidx.compose.material3.CircularProgressIndicator()
        }
        return
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
                    paletteImageBitmap = bitmap!!.asImageBitmap(),
                    onColorChanged = { colorEnvelope: ColorEnvelope ->
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
                            Toast.makeText(context, "Saved!: $value", Toast.LENGTH_SHORT).show()
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
                        onClick = { viewModel.saveColor(selectedHex!!) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryContainerDark,
                            contentColor = onPrimaryContainerDark
                        ),
                        modifier = Modifier.width(160.dp).height(70.dp).padding(8.dp),
                    ) {
                        Text(stringResource(R.string.save_color), style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }
    }
}