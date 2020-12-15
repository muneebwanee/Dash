package com.github.muneebwanee.dash.ui.activities.login

import com.github.muneebwanee.dash.di.PerActivity
import com.github.muneebwanee.dash.ui.activities.base.InterfaceInteractor

/**
 * Created by muneebwanee on 15/12/20.
 */
@PerActivity
interface InterfaceInteractorLogin<V : InterfaceViewLogin> : InterfaceInteractor<V> {
    fun signInDisposable(email: String, pass: String)
}