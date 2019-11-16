package com.example.cameraxdemo.ui.capture

import android.content.SharedPreferences
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.cameraxdemo.R
import com.example.cameraxdemo.ui.analysis.AnalysisViewModel
import kotlinx.android.synthetic.main.fragment_capture.*
import java.io.File
import java.util.concurrent.Executors

class CaptureFragment : Fragment() {


    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var settings: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_capture, container, false)
        this.settings = activity!!.getSharedPreferences("cameraXDemo",0)
        val switchButton = activity!!.findViewById<Switch>(R.id.switchFlash)
        switchButton.setOnCheckedChangeListener(){buttonView,isChecked ->
            val editor = settings.edit()
            if(isChecked) editor.putBoolean("flashState", true) else editor.putBoolean("flashState", false)
            editor.apply()
        }
        return root
    }


    override fun onStart() {
        super.onStart()
        captureViewFinder.post { startCamera() }
    }

    private fun startCamera() {
        val previewConfig = PreviewConfig.Builder().apply {
            setTargetResolution(Size(1920, 1080))
            if(switchLens.isChecked) setLensFacing(CameraX.LensFacing.FRONT) else setLensFacing(CameraX.LensFacing.BACK)
        }.build()

        val preview = Preview(previewConfig)

        preview.setOnPreviewOutputUpdateListener {
            val parent = captureViewFinder.parent as ViewGroup
            parent.removeView(captureViewFinder)
            parent.addView(captureViewFinder, 0)
            captureViewFinder.surfaceTexture = it.surfaceTexture
            updateTransform()
        }

        val imageCaptureConfig = ImageCaptureConfig.Builder()
            .apply {
                setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                //if(flashMode) setFlashMode(FlashMode.ON) else setFlashMode(FlashMode.OFF)
                //if(lensMode) setLensFacing(CameraX.LensFacing.FRONT) else setLensFacing(CameraX.LensFacing.BACK)
            }.build()

        val imageCapture = ImageCapture(imageCaptureConfig)
        captureButton.setOnClickListener {
            val file = File(activity!!.externalMediaDirs.first(), "${System.currentTimeMillis()}.jpg")

            imageCapture.takePicture(file, executor,
                object : ImageCapture.OnImageSavedListener {
                    override fun onError(
                        imageCaptureError: ImageCapture.ImageCaptureError,
                        message: String,
                        exc: Throwable?
                    ) {
                        val msg = "Photo capture failed: $message"
                        Log.e("CameraXApp", msg, exc)
                        captureViewFinder.post {
                            Toast.makeText(activity!!.baseContext, msg, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onImageSaved(file: File) {
                        val msg = "Photo capture succeeded: ${file.absolutePath}"
                        Log.d("CameraXApp", msg)
                        captureViewFinder.post {
                            Toast.makeText(activity!!.baseContext, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        }
        CameraX.bindToLifecycle(this, preview, imageCapture)
    }

    private fun updateTransform() {
        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = captureViewFinder.width / 2f
        val centerY = captureViewFinder.height / 2f

        // Correct preview output to account for display rotation
        val rotationDegrees = when (captureViewFinder.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)
        captureViewFinder.setTransform(matrix)
    }

    private fun addSwitchListeners() {

    }



}