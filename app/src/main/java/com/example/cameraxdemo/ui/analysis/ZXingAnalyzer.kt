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
import com.google.zxing.oned.EAN13Reader
import com.quickbirdstudios.yuv2mat.Yuv
import org.opencv.android.Utils
import org.opencv.core.Mat
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.google.zxing.datamatrix.decoder.Decoder
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class ZXingAnalyzer : ImageAnalysis.Analyzer, Activity() {

    override fun analyze(image: ImageProxy?, rotationDegrees: Int) {
        image.let {
            val m: Mat = Yuv.rgb(it!!.image!!)                                                          //convert image to Mat image (OpenCV)
            val bitmap: Bitmap = Bitmap.createBitmap(m.width(), m.height(),Bitmap.Config.ARGB_8888)     //create a Bitmap
            Utils.matToBitmap(m,bitmap)
            val arr = IntArray(bitmap.width*bitmap.height)
            bitmap.getPixels(arr,0,bitmap.width,0,0,bitmap.width,bitmap.height)              //convert Mat to Bitmap
            val l = RGBLuminanceSource(bitmap.width,bitmap.height,arr)                                  //convert Bitmap to source for ZXing Reader
            try {
                val reader: Result = EAN13Reader().decode(BinaryBitmap(HybridBinarizer(l)))        //decode image
                val intent = Intent("QR-Result")                                                    //create intent with result
                intent.putExtra("qrText", reader.text)
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)                    //send intent with result
            }catch (e: Exception){
                }
        }
    }



}