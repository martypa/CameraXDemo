package com.example.cameraxdemo.ui.analysis

import XZingAnalyzer
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Size
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
    private val executor2 = Executors.newSingleThreadExecutor()

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
        analysisFinder.post { startCamera() }
    }


    private fun startCamera() {
        val previewConfig = PreviewConfig.Builder().apply {
            setTargetResolution(Size(320, 180))
        }.build()

        val preview = Preview(previewConfig)

        preview.setOnPreviewOutputUpdateListener {
            val parent = analysisFinder.parent as ViewGroup
            parent.removeView(analysisFinder)
            parent.addView(analysisFinder, 0)

            analysisFinder.surfaceTexture = it.surfaceTexture
        }

        val analyzerConfig = ImageAnalysisConfig.Builder().apply {
            setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
            setImageQueueDepth(10)
        }.build()

        val analyzeQR = ImageAnalysis(analyzerConfig)

        analyzeQR.setAnalyzer(executor, XZingAnalyzer())



        CameraX.bindToLifecycle(this, preview, analyzeQR)

        val intentFilter = IntentFilter("QR-Result")
        LocalBroadcastManager.getInstance(this.activity!!).registerReceiver(QRRsultReceiver(activity!!), intentFilter)
    }


    class QRRsultReceiver(val contextM: Context): BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            Toast.makeText(contextM, intent?.getStringExtra("qrText"), Toast.LENGTH_SHORT).show()
        }
    }
}