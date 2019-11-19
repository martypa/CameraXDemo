package com.example.cameraxdemo.ui.preview

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Matrix
import android.os.Bundle
import android.util.Size
import android.view.*
import android.widget.Toast
import androidx.camera.core.CameraX
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.Preview
import androidx.camera.core.PreviewConfig
import androidx.camera.view.TextureViewMeteringPointFactory
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.cameraxdemo.R
import kotlinx.android.synthetic.main.fragment_preview.*

class PreviewFragment : Fragment() {

    private val REQUEST_CODE_PERMISSIONS = 10
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_preview, container, false)
        return root
    }

    override fun onStart() {
        super.onStart()
        if (allPermissionsGranted()) {
            viewfinder.post { startCamera() }
        } else {
            ActivityCompat.requestPermissions(
                activity!!,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        viewfinder.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateTransform()
        }
    }

    private fun startCamera() {
        val previewConfig = PreviewConfig.Builder().apply {
            setTargetResolution(Size(1920, 1080))
        }.build()

        val preview = Preview(previewConfig)

        preview.setOnPreviewOutputUpdateListener {
            val parent = viewfinder.parent as ViewGroup
            parent.removeView(viewfinder)
            parent.addView(viewfinder, 0)

            viewfinder.surfaceTexture = it.surfaceTexture
            updateTransform()
        }
        CameraX.bindToLifecycle(this, preview)
        setUpTapToFocus()
    }

    private fun updateTransform() {
        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = viewfinder.width / 2f
        val centerY = viewfinder.height / 2f

        // Correct preview output to account for display rotation
        val rotationDegrees = when (viewfinder.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)
        viewfinder.setTransform(matrix)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                viewfinder.post { startCamera() }
            } else {
                Toast.makeText(activity, "Permissions not granted by the user.", Toast.LENGTH_SHORT)
                    .show()
                activity!!.finish()
            }
        }
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            activity!!.baseContext,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }


    private fun setUpTapToFocus() {
        val cameraControl = CameraX.getCameraControl(CameraX.LensFacing.BACK)
        viewfinder.setOnTouchListener { _, event ->
            if (event.action != MotionEvent.ACTION_UP) {
               return@setOnTouchListener false
            }
            val factory = TextureViewMeteringPointFactory(viewfinder)
            val point = factory.createPoint(event.x, event.y)
            val action = FocusMeteringAction.Builder.from(point).build()
            cameraControl.startFocusAndMetering(action)
            return@setOnTouchListener true
        }
    }






}