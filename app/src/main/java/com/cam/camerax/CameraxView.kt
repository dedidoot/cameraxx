package com.cam.camerax

import android.content.Context
import android.graphics.Matrix
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.camera.core.*
import androidx.lifecycle.LifecycleOwner
import com.cam.camerax.MediaHelper.*
import kotlinx.android.synthetic.main.view_camerax.view.*
import java.io.File

class CameraxView : FrameLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes)

    init {
        inflate(context, R.layout.view_camerax, this)
        resetBtn.setOnClickListener {
            resultImageView.visibility = View.GONE
        }
    }

    fun startCamera(lifecycleOwner: LifecycleOwner) {
        val previewConfig = PreviewConfig.Builder().build()
        val preview = Preview(previewConfig)

        preview.setOnPreviewOutputUpdateListener {
            val parent = textureView.parent as ViewGroup
            parent.removeView(textureView)
            parent.addView(textureView, 0)
            textureView.surfaceTexture = it.surfaceTexture
            updateTransform()
        }

        val imageCaptureConfig = ImageCaptureConfig.Builder().build()

        val imageCapture = ImageCapture(imageCaptureConfig)
        captureBtn.setOnClickListener {
            val aa = getOutputMediaFile(MEDIA_TYPE_IMAGE, "coy")
            val file = File(aa?.absolutePath)
            imageCapture.takePicture(file,
                    object : ImageCapture.OnImageSavedListener {
                        override fun onError(error: ImageCapture.UseCaseError,
                                             message: String, exc: Throwable?) {

                            val msg = "Photo capture failed: $message"
                            Log.e("CameraXx1 ", msg)
                            exc?.printStackTrace()
                        }

                        override fun onImageSaved(file: File) {
                            val msg = "Photo capture succeeded: ${file.absolutePath}"
                            Log.e("CameraXx2", msg)

                            val image = decodeScaledBitmapFromSdCard(file.absolutePath, 1080, 720)
                            resultImageView.setImageBitmap(image)
                            resultImageView.visibility = View.VISIBLE
                        }
                    })
        }
        CameraX.bindToLifecycle(lifecycleOwner, preview, imageCapture)
    }

    private fun updateTransform() {
        val matrix = Matrix()
        textureView.setTransform(matrix)
    }
}