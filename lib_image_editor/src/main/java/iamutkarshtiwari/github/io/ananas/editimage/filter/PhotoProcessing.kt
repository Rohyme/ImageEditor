package iamutkarshtiwari.github.io.ananas.editimage.filter

import android.graphics.Bitmap
import android.graphics.Bitmap.Config


object PhotoProcessing {
    private val TAG = "PhotoProcessing"


    fun filterPhoto(bitmap: Bitmap?, position: Int): Bitmap {
        if (bitmap != null) {
            sendBitmapToNative(bitmap)
        }
        when (position) {
            0 // Original
            -> {
            }
            1 // Instafix
            -> nativeApplyInstafix()
            2 // Ansel
            -> nativeApplyAnsel()
            3 // Testino
            -> nativeApplyTestino()
            4 // XPro
            -> nativeApplyXPro()
            5 // Retro
            -> nativeApplyRetro()
            6 // Black & White
            -> nativeApplyBW()
            7 // Sepia
            -> nativeApplySepia()
            8 // Cyano
            -> nativeApplyCyano()
            9 // Georgia
            -> nativeApplyGeorgia()
            10 // Sahara
            -> nativeApplySahara()
            11 // HDR
            -> nativeApplyHDR()
        }
        val filteredBitmap = getBitmapFromNative(bitmap)
        nativeDeleteBitmap()
        return filteredBitmap
    }

    // /////////////////////////////////////////////
    init {
        System.loadLibrary("photoprocessing")
    }

    external fun nativeInitBitmap(width: Int, height: Int): Int

    external fun nativeGetBitmapRow(y: Int, pixels: IntArray)

    external fun nativeSetBitmapRow(y: Int, pixels: IntArray)

    external fun nativeGetBitmapWidth(): Int

    external fun nativeGetBitmapHeight(): Int

    external fun nativeDeleteBitmap()

    external fun nativeRotate90(): Int

    external fun nativeRotate180()

    external fun nativeFlipHorizontally()

    external fun nativeApplyInstafix()

    external fun nativeApplyAnsel()

    external fun nativeApplyTestino()

    external fun nativeApplyXPro()

    external fun nativeApplyRetro()

    external fun nativeApplyBW()

    external fun nativeApplySepia()

    external fun nativeApplyCyano()

    external fun nativeApplyGeorgia()

    external fun nativeApplySahara()

    external fun nativeApplyHDR()

    external fun nativeLoadResizedJpegBitmap(
        jpegData: ByteArray,
        size: Int, maxPixels: Int
    )

    external fun nativeResizeBitmap(newWidth: Int, newHeight: Int)

    external fun handleSmooth(bitmap: Bitmap, smoothValue: Float)

    external fun handleWhiteSkin(bitmap: Bitmap, whiteValue: Float)

    external fun handleSmoothAndWhiteSkin(bitmap: Bitmap, smoothValue: Float, whiteValue: Float)

    external fun freeBeautifyMatrix()

    private fun sendBitmapToNative(bitmap: Bitmap) {
        val width = bitmap.width
        val height = bitmap.height
        nativeInitBitmap(width, height)
        val pixels = IntArray(width)
        for (y in 0 until height) {
            bitmap.getPixels(pixels, 0, width, 0, y, width, 1)
            nativeSetBitmapRow(y, pixels)
        }
    }

    private fun getBitmapFromNative(bitmap: Bitmap?): Bitmap {
        var bitmap = bitmap
        val width = nativeGetBitmapWidth()
        val height = nativeGetBitmapHeight()

        if (bitmap == null || width != bitmap.width
            || height != bitmap.height || !bitmap.isMutable
        ) { // in
            var config = Config.ARGB_8888
            if (bitmap != null) {
                config = bitmap.config
                bitmap.recycle()
            }
            bitmap = Bitmap.createBitmap(width, height, config)
        }

        val pixels = IntArray(width)
        for (y in 0 until height) {
            nativeGetBitmapRow(y, pixels)
            bitmap!!.setPixels(pixels, 0, width, 0, y, width, 1)
        }

        return bitmap!!
    }

    fun makeBitmapMutable(bitmap: Bitmap): Bitmap {
        sendBitmapToNative(bitmap)
        return getBitmapFromNative(bitmap)
    }

    fun rotate(bitmap: Bitmap, angle: Int): Bitmap {
        var bitmap = bitmap
        val width = bitmap.width
        val height = bitmap.height
        val config = bitmap.config
        nativeInitBitmap(width, height)
        sendBitmapToNative(bitmap)

        if (angle == 90) {
            nativeRotate90()
            bitmap.recycle()
            bitmap = Bitmap.createBitmap(height, width, config)
            bitmap = getBitmapFromNative(bitmap)
            nativeDeleteBitmap()
        } else if (angle == 180) {
            nativeRotate180()
            bitmap.recycle()
            bitmap = Bitmap.createBitmap(width, height, config)
            bitmap = getBitmapFromNative(bitmap)
            nativeDeleteBitmap()
        } else if (angle == 270) {
            nativeRotate180()
            nativeRotate90()
            bitmap.recycle()
            bitmap = Bitmap.createBitmap(height, width, config)
            bitmap = getBitmapFromNative(bitmap)
            nativeDeleteBitmap()
        }
        return bitmap
    }

    fun flipHorizontally(bitmap: Bitmap): Bitmap {
        var bitmap = bitmap
        nativeInitBitmap(bitmap.width, bitmap.height)
        sendBitmapToNative(bitmap)
        nativeFlipHorizontally()
        bitmap = getBitmapFromNative(bitmap)
        nativeDeleteBitmap()
        return bitmap
    }
}
