package com.example.testapp.utils

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.toColorInt
import kotlin.math.abs
import kotlin.math.cbrt
import kotlin.math.pow
import kotlin.math.round

fun buildColorCodes(colorInt: Int): List<Pair<String, String>> {
    val r = Color.red(colorInt)
    val g = Color.green(colorInt)
    val b = Color.blue(colorInt)

    val hex = String.format("#%02X%02X%02X", r, g, b)
    val rgb = "$r, $g, $b"
    val cmyk = rgbToCmykString(r, g, b)
    val hsl = rgbToHslString(r, g, b)
    val hsv = rgbToHsvString(r, g, b)
    val lab = rgbToLabString(r, g, b)

    return listOf(
        "HEX" to hex,
        "RGB" to rgb,
        "CMYK" to cmyk,
        "HSL" to hsl,
        "HSV" to hsv,
        "LAB" to lab
    )
}

fun rgbToCmykString(r: Int, g: Int, b: Int): String {
    val rn = r / 255.0
    val gn = g / 255.0
    val bn = b / 255.0

    val k = 1 - maxOf(rn, gn, bn)
    if (k == 1.0) return "CMYK(0%, 0%, 0%, 100%)"

    val c = (1 - rn - k) / (1 - k)
    val m = (1 - gn - k) / (1 - k)
    val y = (1 - bn - k) / (1 - k)

    return "${round(c * 100).toInt()}%, ${round(m * 100).toInt()}%, ${round(y * 100).toInt()}%, ${round(k * 100).toInt()}%"
}

fun rgbToHslString(r: Int, g: Int, b: Int): String {
    val rn = r / 255.0
    val gn = g / 255.0
    val bn = b / 255.0

    val max = maxOf(rn, gn, bn)
    val min = minOf(rn, gn, bn)
    val delta = max - min

    val lightness = (max + min) / 2
    val saturation = if (delta == 0.0) 0.0 else delta / (1 - abs(2 * lightness - 1))

    var hue = when {
        delta == 0.0 -> 0.0
        max == rn -> 60 * (((gn - bn) / delta) % 6)
        max == gn -> 60 * (((bn - rn) / delta) + 2)
        else -> 60 * (((rn - gn) / delta) + 4)
    }
    if (hue < 0) hue += 360.0

    return "${round(hue).toInt()}°, ${(round(saturation * 100).toInt())}%, ${(round(lightness * 100).toInt())}%"
}

fun rgbToHsvString(r: Int, g: Int, b: Int): String {
    val hsv = FloatArray(3)
    Color.RGBToHSV(r, g, b, hsv)
    return "${round(hsv[0]).toInt()}°, ${(round(hsv[1] * 100)).toInt()}%, ${round((hsv[2] * 100)).toInt()}%"
}
fun rgbToLabString(r: Int, g: Int, b: Int): String{
    var red = r / 255.0
    var green = g / 255.0
    var blue = b / 255.0

    fun gammaCorrect(channel: Double): Double {
        return if (channel <= 0.04045) {
            channel / 12.92
        } else {
            ((channel + 0.055) / 1.055).pow(2.4)
        }
    }
    red = gammaCorrect(red)
    green = gammaCorrect(green)
    blue = gammaCorrect(blue)

    val x = red * 0.4123907992659595 + green * 0.357584339383878 + blue * 0.180480788401834
    val y = red * 0.2126390058715103 + green * 0.715168678767756 + blue * 0.072192315360734
    val z = red * 0.0193308187155918 + green * 0.119194779794429 + blue * 0.950532152249661

    val xn = 0.9504559270516716
    val yn = 1
    val zn = 1.0890577507598784

    fun f(t: Double): Double {
        val delta = 6.0 / 29.0
        return if (t > delta * delta * delta) {
            cbrt(t)
        } else {
            t / (3 * delta * delta) + 4.0 / 29.0
        }
    }

    val fx = f(x / xn)
    val fy = f(y / yn)
    val fz = f(z / zn)

    val L = round(116.0 * fy - 16.0).toInt()
    val A = round(500.0 * (fx - fy)).toInt()
    val B = round(200.0 * (fy - fz)).toInt()

    return "$L, $A, $B"
}
fun isDarkColor(hex: String): Boolean {
    val color = hex.toColorInt()
    val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
    return darkness >= 0.5
}

fun getColorAtPosition(bitmap: Bitmap?, x: Int, y: Int): String? {
    if (bitmap == null || x < 0 || y < 0 || x >= bitmap.width || y >= bitmap.height) {
        return null
    }
    val pixel = bitmap.getPixel(x, y)
    return String.format("#%06X", 0xFFFFFF and pixel)
}