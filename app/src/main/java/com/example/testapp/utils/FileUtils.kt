package com.example.testapp.utils

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

fun saveBitmapToFile(context: Context, bitmap: Bitmap): String {
    val file = File(context.cacheDir, "photo_${System.currentTimeMillis()}.jpg")
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
    }
    return file.absolutePath
}

fun deletePhotoFile(path: String?) {
    if (path.isNullOrBlank()) return
    runCatching {
        val file = File(path)
        if (file.exists()) file.delete()
    }
}



