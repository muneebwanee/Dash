package com.github.muneebwanee.dash.di.component

import com.github.muneebwanee.dash.di.PerService
import com.github.muneebwanee.dash.di.component.AppComponent
import com.github.muneebwanee.dash.di.module.ServiceModule
import com.github.muneebwanee.dash.services.calls.CallsService
import com.github.muneebwanee.dash.services.sms.SmsService
import com.github.muneebwanee.dash.services.social.MonitorService
import dagger.Component

/**
 * Created by muneebwanee on 15/12/20.
 */
@PerService
@Component(dependencies = [AppComponent::class], modules = [ServiceModule::class])
interface ServiceComponent {

    fun inject(callsService: CallsService)
    fun inject(smsService: SmsService)
    fun inject(monitorService: MonitorService)

}