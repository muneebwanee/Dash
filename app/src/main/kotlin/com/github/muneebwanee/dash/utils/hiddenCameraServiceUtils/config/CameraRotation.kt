package com.github.muneebwanee.dash.utils.hiddenCameraServiceUtils.config

import androidx.annotation.IntDef

/**
 * Created by muneebwanee on 15/12/20.
 */
class CameraRotation {

    init {
        throw RuntimeException("Cannot initialize this class.")
    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(ROTATION_0, ROTATION_90, ROTATION_180, ROTATION_270)
    annotation class SupportedRotation

    companion object {
        const val ROTATION_90 = 90
        const val ROTATION_180 = 180
        const val ROTATION_270 = 270
        const val ROTATION_0 = 0
    }
}
