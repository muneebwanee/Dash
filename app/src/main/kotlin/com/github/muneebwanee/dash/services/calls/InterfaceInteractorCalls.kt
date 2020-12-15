package com.github.muneebwanee.dash.services.calls

import com.github.muneebwanee.dash.di.PerService
import com.github.muneebwanee.dash.services.base.InterfaceInteractorService

/**
 * Created by muneebwanee on 15/12/20.
 */
@PerService
interface InterfaceInteractorCalls<S : InterfaceServiceCalls> : InterfaceInteractorService<S> {

    fun startRecording(phoneNumber:String?,type:Int)
    fun stopRecording()

}