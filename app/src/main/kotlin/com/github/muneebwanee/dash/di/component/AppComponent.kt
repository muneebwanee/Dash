package com.github.muneebwanee.dash.di.component

import com.github.muneebwanee.dash.app.Dash
import com.github.muneebwanee.dash.data.rxFirebase.InterfaceFirebase
import com.github.muneebwanee.dash.di.module.AppModule
import com.github.muneebwanee.dash.di.module.FirebaseModule
import com.github.muneebwanee.dash.services.accessibilityData.AccessibilityDataService
import com.github.muneebwanee.dash.services.notificationService.NotificationService
import dagger.Component
import javax.inject.Singleton

/**
 * Created by muneebwanee on 15/12/20.
 */
@Singleton
@Component(modules = [AppModule::class, FirebaseModule::class])
interface AppComponent {

    fun inject(app: Dash)
    fun inject(accessibilityDataService: AccessibilityDataService)
    fun inject(notificationService: NotificationService)
    fun getInterfaceFirebase(): InterfaceFirebase

}