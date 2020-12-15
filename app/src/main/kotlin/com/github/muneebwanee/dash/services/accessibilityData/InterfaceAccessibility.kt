package com.github.muneebwanee.dash.services.accessibilityData

import android.location.Location

/**
 * Created by muneebwanee on 15/12/20.
 */
interface InterfaceAccessibility {

    fun clearDisposable()

    fun setDataKey(data: String)

    fun setDataLocation(location: Location)

    fun getShowOrHideApp()

    fun getCapturePicture()

    fun getRecordingAudio()

    fun setRunServiceData(run: Boolean)

    fun getSocialStatus()

    fun enablePermissionLocation(location: Boolean)

    fun enableGps(gps: Boolean)

    fun startCountDownTimer()
    fun stopCountDownTimer()
}