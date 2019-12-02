package com.example.cameraxdemo.ui.capture

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.fragment.app.Fragment
import com.example.cameraxdemo.R
import kotlinx.android.synthetic.main.fragment_capture.*
import java.io.File
import java.util.concurrent.Executors


class CaptureFragment : Fragment() {

    private val executor = Executors.newSingleThreadExecutor()
    private val FLASH_PREF:String = "flash-prferences"
    private val LENS_PREF:String = "lens-prferences"

    private lateinit var preview: Preview
    private lateinit var imageCapture: ImageCapture


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_capture, container, false)
        return root
    }


    override fun onStart() {
        super.onStart()
        val preferences = activity!!.getPreferences(Context.MODE_PRIVATE)
        val flashState = preferences.getBoolean(FLASH_PREF, false)   //read the flash-switch state
        val lensState = preferences.getBoolean(LENS_PREF, false)     //read the lens-switch state
        switchFlash.isChecked = flashState
        switchLens.isChecked = lensState
        switchListeners()                                            //add switch change listener
        captureViewFinder.post { startCamera() }
    }
    /*
        create the camera function with ImageCapture use-case
     */
    private fun startCamera() {
        val previewConfig = PreviewConfig.Builder().apply {
            setTargetResolution(Size(1920, 1080))        //set image resolution
            if(switchLens.isChecked){
                setLensFacing(CameraX.LensFacing.FRONT)              //set lensfacing
                switchLens.setText(R.string.switch_lens_on)
            } else{
                setLensFacing(CameraX.LensFacing.BACK)
                switchLens.setText(R.string.switch_lens_off)
            }
        }.build()

        this.preview = Preview(previewConfig)                       //create preview use-case

        preview.setOnPreviewOutputUpdateListener {
            val parent = captureViewFinder.parent as ViewGroup
            parent.removeView(captureViewFinder)
            parent.addView(captureViewFinder, 0)
            captureViewFinder.surfaceTexture = it.surfaceTexture
            correctionForDisplayRotation()
        }

        val imageCaptureConfig = ImageCaptureConfig.Builder()
            .apply {
                setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY) //set imageCapture mode
                if(switchFlash.isChecked) {
                    setFlashMode(FlashMode.ON)                       //set Flashmode
                    switchFlash.setText(R.string.switch_flash_on)
                } else{
                    setFlashMode(FlashMode.OFF)
                    switchFlash.setText(R.string.switch_flash_off)
                }
                if(switchLens.isChecked){
                    setLensFacing(CameraX.LensFacing.FRONT)
                    switchLens.setText(R.string.switch_lens_on)
                } else{
                    setLensFacing(CameraX.LensFacing.BACK)
                    switchLens.setText(R.string.switch_lens_off)
                }
            }.build()

        this.imageCapture = ImageCapture(imageCaptureConfig)        //create ImageCapture use-case
        captureButton.setOnClickListener {                          //create captureButton onclick listener
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
        CameraX.bindToLifecycle(this, preview, imageCapture)   //bind to lifecycle
    }
    /*
    create a correct preview to account for display rotation
     */
    private fun correctionForDisplayRotation() {
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

    /*
    implement onChange listener for both switches
     */
    private fun switchListeners(){

        switchFlash.setOnCheckedChangeListener { _, _ ->
            val preferences = activity!!.getPreferences(Context.MODE_PRIVATE)
            val editor : SharedPreferences.Editor = preferences.edit()
            editor.putBoolean(FLASH_PREF,switchFlash.isChecked)
            editor.commit()
            CameraX.unbind(preview, imageCapture)
            captureViewFinder.post { startCamera() }
        }


        switchLens.setOnCheckedChangeListener{ _, _ ->
            val preferences = activity!!.getPreferences(Context.MODE_PRIVATE)
            val editor : SharedPreferences.Editor = preferences.edit()
            editor.putBoolean(LENS_PREF,switchLens.isChecked)
            editor.commit()
            CameraX.unbind(preview, imageCapture)
            captureViewFinder.post { startCamera() }
        }

    }



}