package com.github.muneebwanee.dash.utils.hiddenCameraServiceUtils

import android.content.Context
import com.github.muneebwanee.dash.utils.hiddenCameraServiceUtils.config.CameraFacing
import com.github.muneebwanee.dash.utils.hiddenCameraServiceUtils.config.CameraImageFormat
import com.github.muneebwanee.dash.utils.hiddenCameraServiceUtils.config.CameraResolution
import com.github.muneebwanee.dash.utils.hiddenCameraServiceUtils.config.CameraRotation
import com.github.muneebwanee.dash.utils.hiddenCameraServiceUtils.HiddenCameraUtils.getFileName

import java.io.File

/**
 * Created by muneebwanee on 15/12/20.
 */
class CameraConfig {

    private var context: Context? = null

    @CameraResolution.SupportedResolution
    @get:CameraResolution.SupportedResolution
    internal var resolution = CameraResolution.MEDIUM_RESOLUTION
        private set

    @CameraFacing.SupportedCameraFacing
    @get:CameraFacing.SupportedCameraFacing
    internal var facing = CameraFacing.FRONT_FACING_CAMERA
        private set

    @CameraImageFormat.SupportedImageFormat
    @get:CameraImageFormat.SupportedImageFormat
    internal var imageFormat = CameraImageFormat.FORMAT_JPEG
        private set

    @CameraRotation.SupportedRotation
    @get:CameraRotation.SupportedRotation
    internal var imageRotation = CameraRotation.ROTATION_0
        private set

    internal var imageFile: File?=null
            private set

    fun builder(context: Context): Builder {
        this.context = context
        return Builder()
    }

    inner class Builder {

        fun setCameraFacing(@CameraFacing.SupportedCameraFacing cameraFacing: Int): Builder {
            facing = cameraFacing
            return this
        }

        fun setImageRotation(@CameraRotation.SupportedRotation rotation: Int): Builder {

            if (rotation != CameraRotation.ROTATION_0
                    && rotation != CameraRotation.ROTATION_90
                    && rotation != CameraRotation.ROTATION_180
                    && rotation != CameraRotation.ROTATION_270)
                imageRotation = CameraRotation.ROTATION_0
            else imageRotation = rotation
            return this
        }

        fun build(): CameraConfig {
            if (imageFile==null) imageFile = File(context!!.getFileName())
            return this@CameraConfig
        }
    }
}
