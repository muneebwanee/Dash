package com.github.muneebwanee.dash.services.base

import com.github.muneebwanee.dash.di.component.ServiceComponent
import io.reactivex.disposables.Disposable

/**
 * Created by muneebwanee on 15/12/20.
 */
interface InterfaceService {

    fun getComponent(): ServiceComponent?

    fun addDisposable(disposable: Disposable)

    fun clearDisposable()

}