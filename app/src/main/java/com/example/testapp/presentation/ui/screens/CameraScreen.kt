package com.example.testapp.presentation.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.compose.onPrimaryContainerDark
import com.example.compose.primaryContainerDark
import com.example.testapp.presentation.Screen
import com.example.testapp.utils.deletePhotoFile
import com.example.testapp.utils.saveBitmapToFile
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.util.concurrent.Executors
import com.example.testapp.R

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    navController: NavController,
    mode: String
) {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var photoPath by remember { mutableStateOf<String?>(null) }

    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    fun resetCaptureAndDelete() {
        cameraProvider?.unbindAll()
        deletePhotoFile(photoPath)
        capturedBitmap = null
        photoPath = null
    }

    fun navigateToHome() {
        cameraProvider?.unbindAll()
        navController.navigate(Screen.Home.route) {
            popUpTo(Screen.Home.route) { inclusive = true }
            launchSingleTop = true
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraProvider?.unbindAll()
            cameraExecutor.shutdown()
        }
    }

    if (!cameraPermissionState.status.isGranted) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(stringResource(R.string.camera_permission_text))
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                    Text(stringResource(R.string.grant_camera_permission))
                }
            }
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (capturedBitmap == null) {
            CameraCaptureView(
                onCameraProviderReady = { provider ->
                    cameraProvider = provider
                },
                cameraExecutor = cameraExecutor,
                onImageCaptured = { bitmap, path ->
                    capturedBitmap = bitmap
                    photoPath = path
                }
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize().background(Color.Black),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color.Black)
                ) {
                    Image(
                        bitmap = capturedBitmap!!.asImageBitmap(),
                        contentDescription = "Photo",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Button(
                        onClick = {
                            val path = photoPath ?: return@Button
                            cameraProvider?.unbindAll()
                            if (mode == "color") {
                                navController.navigate(Screen.PickColor.passPath(path))
                            } else {
                                navController.navigate(Screen.PickPalette.passPath(path))
                            }
                        },
                        modifier = Modifier.weight(1f).height(56.dp),
                        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryContainerDark,
                            contentColor = onPrimaryContainerDark
                        )
                    ) {
                        Text(stringResource(R.string.choose_photo),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            softWrap = true,
                            style = MaterialTheme.typography.bodyLarge)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { resetCaptureAndDelete() },
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryContainerDark,
                            contentColor = onPrimaryContainerDark
                        )
                    ) {
                        Text(stringResource(R.string.retake), style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }

        IconButton(
            onClick = { navigateToHome() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = "Back to home",
                tint = Color.White
            )
        }
    }
}

@Composable
fun CameraCaptureView(
    onCameraProviderReady: (ProcessCameraProvider) -> Unit,
    cameraExecutor: java.util.concurrent.ExecutorService,
    onImageCaptured: (Bitmap, String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val imageCapture = remember { ImageCapture.Builder().build() }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FIT_CENTER

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        onCameraProviderReady(cameraProvider)

                        val preview = Preview.Builder().build()
                        preview.surfaceProvider = surfaceProvider

                        val cameraSelector = if (cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)) {
                            CameraSelector.DEFAULT_BACK_CAMERA
                        } else {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        }

                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture
                        )
                    }, ContextCompat.getMainExecutor(ctx))
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        Button(
            onClick = {
                imageCapture.takePicture(
                    cameraExecutor,
                    object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            val bitmap = imageProxyToBitmap(image)
                            val path = saveBitmapToFile(context, bitmap)
                            onImageCaptured(bitmap, path)
                            image.close()
                        }

                        override fun onError(exception: ImageCaptureException) {
                            exception.printStackTrace()
                        }
                    }
                )
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryContainerDark,
                contentColor = onPrimaryContainerDark
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .width(250.dp)
                .height(56.dp)
        ) {
            Text(stringResource(R.string.take_photo), style = MaterialTheme.typography.titleLarge)
        }
    }
}

fun imageProxyToBitmap(image: ImageProxy): Bitmap {
    val buffer = image.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    val rawBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    val rotationDegrees = image.imageInfo.rotationDegrees
    val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
    return Bitmap.createBitmap(rawBitmap, 0, 0, rawBitmap.width, rawBitmap.height, matrix, true)
}