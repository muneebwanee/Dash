package com.github.muneebwanee.dash.ui.fragments.recording

import com.github.muneebwanee.dash.di.PerActivity
import com.github.muneebwanee.dash.ui.activities.base.InterfaceInteractor

/**
 * Created by muneebwanee on 15/12/20.
 */
@PerActivity
interface InterfaceInteractorRecording <V : InterfaceViewRecording> : InterfaceInteractor<V> {
    fun stopAudioRecordHolder()
    fun getRecordAudio(time:Long)
    fun valueEventEnableRecording()
    fun valueEventTimerRecording()
}