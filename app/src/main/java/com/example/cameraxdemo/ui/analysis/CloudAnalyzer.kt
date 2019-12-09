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












var options = FirebaseVisionBarcodeDetectorOptions.Builder()
    .setBarcodeFormats(
        FirebaseVisionBarcode.FORMAT_EAN_13,
        FirebaseVisionBarcode.FORMAT_EAN_8
    )
    .build()


class CloudAnalyzer : ImageAnalysis.Analyzer, Activity() {

    override fun analyze(image: ImageProxy?, rotationDegrees: Int) {
        image.let {
            val m: Mat = Yuv.rgb(it!!.image!!)                                                          //convert image to Mat image (OpenCV)
            val bitmap: Bitmap = Bitmap.createBitmap(m.width(), m.height(),Bitmap.Config.ARGB_8888)     //create a Bitmap
            val image = FirebaseVisionImage.fromBitmap(bitmap)                                          //create a FirebaseBisionImage object from a Bitmap object

            val detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);


            val result = detector.detectInImage(image)
                .addOnSuccessListener {
                    // Task completed successfully
                    val intent = Intent("Task completed successfully")
                }
                .addOnFailureListener {
                    // Task failed with an exception
                    intent = Intent("Task failed with an exception")
                }


            //val reader: Result = DataMatrixReader().decode(BinaryBitmap(HybridBinarizer(l)))        //decode image
                val intent = Intent("QR-Result")                                                  //create intent with result
                //intent.putExtra("qrText", result)
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)                    //send intent with result

        }
    }





}