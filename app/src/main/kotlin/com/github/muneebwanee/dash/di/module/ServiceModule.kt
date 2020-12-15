package com.github.muneebwanee.dash.di.module

import android.app.Service
import android.content.Context
import com.github.muneebwanee.dash.di.PerService
import com.github.muneebwanee.dash.services.calls.InteractorCalls
import com.github.muneebwanee.dash.services.calls.InterfaceInteractorCalls
import com.github.muneebwanee.dash.services.calls.InterfaceServiceCalls
import com.github.muneebwanee.dash.services.sms.InteractorSms
import com.github.muneebwanee.dash.services.sms.InterfaceInteractorSms
import com.github.muneebwanee.dash.services.sms.InterfaceServiceSms
import dagger.Module
import dagger.Provides

/**
 * Created by muneebwanee on 15/12/20.
 */
@Module
class ServiceModule(var service:Service) {

    @Provides
    fun provideContext(): Context = service.applicationContext

    @Provides
    @PerService
    fun provideInterfaceInteractorCalls(interactor: InteractorCalls<InterfaceServiceCalls>): InterfaceInteractorCalls<InterfaceServiceCalls> = interactor

    @Provides
    @PerService
    fun provideInterfaceInteractorSms(interactor: InteractorSms<InterfaceServiceSms>): InterfaceInteractorSms<InterfaceServiceSms> = interactor

}