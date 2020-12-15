package com.github.muneebwanee.dash.services.base

import android.content.Context
import com.github.muneebwanee.dash.data.rxFirebase.InterfaceFirebase
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

/**
 * Created by muneebwanee on 15/12/20.
 */
open class BaseInteractorService<S : InterfaceService> @Inject constructor(private var context: Context, private var firebase: InterfaceFirebase) : InterfaceInteractorService<S> {

    private var service: S? = null

    override fun onAttach(service: S) {
        this.service = service
    }

    override fun onDetach() {
        this.service = null
    }

    override fun getService(): S? = service

    override fun isNullService(): Boolean = service != null

    override fun getContext(): Context = context

    override fun firebase(): InterfaceFirebase = firebase

    override fun user(): FirebaseUser? = firebase.getUser()

}