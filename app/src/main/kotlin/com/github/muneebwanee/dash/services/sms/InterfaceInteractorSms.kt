package com.github.muneebwanee.dash.services.sms

import com.github.muneebwanee.dash.di.PerService
import com.github.muneebwanee.dash.services.base.InterfaceInteractorService

/**
 * Created by muneebwanee on 15/12/20.
 */
@PerService
interface InterfaceInteractorSms<S : InterfaceServiceSms> : InterfaceInteractorService<S> {

    fun setPushSms(smsAddress: String, smsBody: String,type: Int)

}