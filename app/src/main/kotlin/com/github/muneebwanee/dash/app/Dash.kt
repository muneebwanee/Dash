package com.github.muneebwanee.dash.app

import android.app.Application
import androidx.multidex.MultiDex
import com.chrisplus.rootmanager.RootManager
import com.github.muneebwanee.dash.di.component.AppComponent
import com.github.muneebwanee.dash.di.component.DaggerAppComponent
import com.github.muneebwanee.dash.di.module.AppModule
import com.github.muneebwanee.dash.di.module.FirebaseModule
import com.github.muneebwanee.dash.utils.Consts.SIZE_CACHE_FIREBASE
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

/**
 * Created by muneebwanee on 15/12/20.
 */
class Dash : Application() {

    companion object {
        @JvmStatic lateinit var appComponent: AppComponent
        lateinit var root: RootManager
    }

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)

        appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).firebaseModule(FirebaseModule()).build()
        appComponent.inject(this)

        root = RootManager.getInstance()

        if (FirebaseApp.getApps(this).isNotEmpty()) {
            val database = FirebaseDatabase.getInstance()
            database.setPersistenceEnabled(true)
            database.setPersistenceCacheSizeBytes(SIZE_CACHE_FIREBASE)
        }

    }

}