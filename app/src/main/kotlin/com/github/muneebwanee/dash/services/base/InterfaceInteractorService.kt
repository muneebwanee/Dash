package com.github.muneebwanee.dash.services.base

import android.content.Context
import com.github.muneebwanee.dash.data.rxFirebase.InterfaceFirebase
import com.google.firebase.auth.FirebaseUser

/**
 * Created by muneebwanee on 15/12/20.
 */
interface InterfaceInteractorService<S : InterfaceService> {

    fun onAttach(service: S)

    fun onDetach()

    fun getService(): S?

    fun isNullService() : Boolean

    fun getContext(): Context

    fun firebase(): InterfaceFirebase

    fun user(): FirebaseUser?

}