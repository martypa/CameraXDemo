package com.example.cameraxdemo.ui.analysis

import android.content.Context
import android.graphics.Bitmap
import android.media.Image
import android.util.SparseArray
import android.widget.Toast
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.nio.ByteBuffer

class QRCodeAnalyzer(newContext: Context) : ImageAnalysis.Analyzer{

    init {
        setContext(newContext)
    }

    override fun analyze(image: ImageProxy?, rotationDegrees: Int) {

        val detector = BarcodeDetector.Builder(context).setBarcodeFormats(Barcode.DATA_MATRIX).build()


        var barcode: SparseArray<Barcode>? = null
        (if (image != null) {
                val frame: Frame? = Frame.Builder().setRotation(rotationDegrees).setBitmap(decoteToBitmap(image.image!!)).build()
                barcode = detector.detect(frame)
            }
        )
        (if (barcode != null) {
            Toast.makeText(context,barcode.toString(),Toast.LENGTH_LONG)
            }
        )
    }

    private fun decoteToBitmap(img: Image): Bitmap{
        val planes = img.planes
        val pixelStride = planes[0].pixelStride
        val rowStride = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * img.width

        val bitmap: Bitmap = Bitmap.createBitmap(img.width + rowPadding / pixelStride, img.height, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(planes[0].buffer)
        img.close()
        return Bitmap.createBitmap(bitmap, 0,0, img.width, img.height, null, true)
    }


        companion object {
            private lateinit var context: Context
            fun setContext(con: Context) {
                context = con
            }
        }


}