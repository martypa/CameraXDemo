import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.zxing.BinaryBitmap
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.datamatrix.DataMatrixReader
import com.quickbirdstudios.yuv2mat.Yuv
import org.opencv.android.Utils
import org.opencv.core.Mat


class XZingAnalyzer : ImageAnalysis.Analyzer, Activity() {

    override fun analyze(image: ImageProxy?, rotationDegrees: Int) {
        image.let {
            val m: Mat = Yuv.rgb(it!!.image!!)
            val bitmap: Bitmap = Bitmap.createBitmap(m.width(), m.height(),Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(m,bitmap)
            val arr = IntArray(bitmap.width*bitmap.height)
            bitmap.getPixels(arr,0,bitmap.width,0,0,bitmap.width,bitmap.height)
            val l = RGBLuminanceSource(bitmap.width,bitmap.height,arr)
            try {
                val reader: Result = DataMatrixReader().decode(BinaryBitmap(HybridBinarizer(l)))
                val intent = Intent("QR-Result")
                intent.putExtra("qrText", reader.text)
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            }catch (e: Exception){

            }
        }
    }



}