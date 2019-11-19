package com.example.cameraxdemo.ui.analysis

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.fragment.app.Fragment
import com.example.cameraxdemo.R
import kotlinx.android.synthetic.main.fragment_analysis.*
import java.util.concurrent.Executors

class AnalysisFragment : Fragment() {

    private val executor = Executors.newSingleThreadExecutor()

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
            setDefaultResolution(Size(320, 180))
        }.build()

        val preview = Preview(previewConfig)

        preview.setOnPreviewOutputUpdateListener {
            val parent = analysisFinder.parent as ViewGroup
            parent.removeView(analysisFinder)
            parent.addView(analysisFinder, 0)

            analysisFinder.surfaceTexture = it.surfaceTexture
        }

        val analyzerConfig = ImageAnalysisConfig.Builder().apply {
            // In our analysis, we care more about the latest image than
            // analyzing *every* image
            setImageReaderMode(
                ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
        }.build()

        // Build the image analysis use case and instantiate our analyzer
        val analyzerUseCase = ImageAnalysis(analyzerConfig).apply {
            setAnalyzer(executor, QRCodeAnalyzer(activity!!))
        }

        CameraX.bindToLifecycle(this, preview, analyzerUseCase)
    }

}