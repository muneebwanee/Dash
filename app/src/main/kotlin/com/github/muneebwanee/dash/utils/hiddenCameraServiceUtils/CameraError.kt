package com.github.muneebwanee.dash.utils.hiddenCameraServiceUtils

import androidx.annotation.IntDef

/**
 * Created by muneebwanee on 15/12/20.
 */
class CameraError private constructor() {

    init {
        throw RuntimeException("Cannot initiate CameraError.")
    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(ERROR_CAMERA_PERMISSION_NOT_AVAILABLE, ERROR_CAMERA_OPEN_FAILED, ERROR_DOES_NOT_HAVE_FRONT_CAMERA, ERROR_DOES_NOT_HAVE_OVERDRAW_PERMISSION, ERROR_IMAGE_WRITE_FAILED)
    annotation class CameraErrorCodes

    companion object {

        const val ERROR_CAMERA_OPEN_FAILED = 1122
        const val ERROR_CAMERA_PERMISSION_NOT_AVAILABLE = 5472
        const val ERROR_DOES_NOT_HAVE_OVERDRAW_PERMISSION = 3136
        const val ERROR_DOES_NOT_HAVE_FRONT_CAMERA = 8722
        const val ERROR_IMAGE_WRITE_FAILED = 9854
    }
}