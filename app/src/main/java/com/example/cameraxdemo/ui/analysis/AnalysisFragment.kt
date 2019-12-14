package com.example.cameraxdemo.ui.analysis

import CloudAnalyzer
import ZXingAnalyzer
import TextAnalyzer
import android.content.*
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.cameraxdemo.R
import kotlinx.android.synthetic.main.fragment_analysis.*
import java.util.concurrent.Executors

class AnalysisFragment : Fragment() {

    private val executor = Executors.newSingleThreadExecutor()
    private val ANALYZER_PREFS:String = "analyzer-prefs"
    private lateinit var analyzeQR: ImageAnalysis

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_analysis, container, false)
        return root
    }


    override fun onStart() {
        super.onStart()
        val preferences = activity!!.getPreferences(Context.MODE_PRIVATE)
        val analyzerState = preferences.getBoolean(ANALYZER_PREFS, false)
        analyzerSwitch.isChecked = analyzerState                              //read switch state
        addListener()                                                         //add switch onChange listener
        analysisFinder.post { startCamera(analyzerSwitch.isChecked) }
    }


    private fun startCamera(withAnalyzer: Boolean) {
        val previewConfig = PreviewConfig.Builder().apply {
            setTargetResolution(Size(1920, 1080))                     //set image resolution
        }.build()

        val preview = Preview(previewConfig)                                    //create preview use-case

        preview.setOnPreviewOutputUpdateListener {
            val parent = analysisFinder.parent as ViewGroup
            parent.removeView(analysisFinder)
            parent.addView(analysisFinder, 0)

            analysisFinder.surfaceTexture = it.surfaceTexture
        }

        val analyzerConfig = ImageAnalysisConfig.Builder().apply {
            setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)          //set imageReaderMode
            setImageQueueDepth(10)                                                          //set imageQueue size
        }.build()

        this.analyzeQR = ImageAnalysis(analyzerConfig)                                      //create ImageAnalysis use-case
        if(withAnalyzer) {
            //analyzeQR.setAnalyzer(executor, ZXingAnalyzer())                                //setAnalyzer with thread executor and ZXingAnalyzer
            //analyzeQR.setAnalyzer(executor, CloudAnalyzer())                                //setAnalyzer with thread executor and ZXingAnalyzer
            analyzeQR.setAnalyzer(executor, TextAnalyzer())                                //setAnalyzer with thread executor and ZXingAnalyzer


        }

        CameraX.bindToLifecycle(this, preview, analyzeQR)                       //bind to lifecycle

        val intentFilter = IntentFilter("QR-Result")
        LocalBroadcastManager.getInstance(this.activity!!).registerReceiver(QRRsultReceiver(activity!!), intentFilter)  //create LocalBroadcastManager for ZXing results
    }

    /*
    implement onChange listener for the switch
     */
    fun addListener(){
        analyzerSwitch.setOnCheckedChangeListener { _, isChecked ->
            val preferences = activity!!.getPreferences(Context.MODE_PRIVATE)
            val editor : SharedPreferences.Editor = preferences.edit()
            editor.putBoolean(ANALYZER_PREFS,analyzerSwitch.isChecked)
            editor.commit()
            if(isChecked){
                //this.analyzeQR.setAnalyzer(executor, ZXingAnalyzer())
                //this.analyzeQR.setAnalyzer(executor, CloudAnalyzer())
                this.analyzeQR.setAnalyzer(executor, TextAnalyzer())


            }else{
                this.analyzeQR.removeAnalyzer()
            }
        }
    }

    /*
    Intent Receiver which display the datamatrix result as toast
     */
    inner class QRRsultReceiver(val contextM: Context): BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val toast: Toast = Toast.makeText(contextM, intent?.getStringExtra("qrText"), Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.HORIZONTAL_GRAVITY_MASK,0,0)
            toast.show()
        }
    }
}