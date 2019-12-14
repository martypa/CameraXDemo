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
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.R.attr.rotation
import android.graphics.ImageFormat
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
//import jdk.nashorn.internal.objects.ArrayBufferView.buffer
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import kotlinx.android.synthetic.main.activity_into.view.*


class TextAnalyzer : ImageAnalysis.Analyzer, Activity() {

    override fun analyze(image: ImageProxy?, rotationDegrees: Int) {
        image.let {
            val imageraw = it!!.image!!
            val m: Mat = Yuv.rgb(it!!.image!!)                                                          //convert image to Mat image (OpenCV)
            val bitmap: Bitmap = Bitmap.createBitmap(m.width(), m.height(),Bitmap.Config.ARGB_8888)     //create a Bitmap
            Utils.matToBitmap(m,bitmap)
            val arr = IntArray(bitmap.width*bitmap.height)
            bitmap.getPixels(arr,0,bitmap.width,0,0,bitmap.width,bitmap.height)              //convert Mat to Bitmap
            val l = RGBLuminanceSource(bitmap.width,bitmap.height,arr)

            val image = FirebaseVisionImage.fromBitmap(bitmap)                                          //create a FirebaseBisionImage object from a Bitmap object


            val detector = FirebaseVision.getInstance()
                .onDeviceTextRecognizer

            val detector2 = FirebaseVision.getInstance().cloudTextRecognizer
// Or, to change the default settings:
// val detector = FirebaseVision.getInstance().getCloudTextRecognizer(options)


            val result = detector2.processImage(image)
                .addOnSuccessListener { results ->
                        var result_text = results.text
                        Log.d("TAG", result_text)

                        }


                    // Task completed successfully
                    // ...

                .addOnFailureListener {
                    // Task failed with an exception
                    // ...
                    print("fail in Listener")
                }





            //val reader: Result = DataMatrixReader().decode(BinaryBitmap(HybridBinarizer(l)))        //decode image
                //intent.putExtra("qrText", "this is a cloud test")
                //LocalBroadcastManager.getInstance(this).sendBroadcast(intent)                    //send intent with result

        }
    }





}