package com.github.muneebwanee.dash.ui.fragments.keylog

import com.github.muneebwanee.dash.di.PerActivity
import com.github.muneebwanee.dash.ui.activities.base.InterfaceInteractor

/**
 * Created by muneebwanee on 15/12/20.
 */
@PerActivity
interface InterfaceInteractorKeys<V : InterfaceViewKeys> : InterfaceInteractor<V> {
    fun valueEventEnableKeys()
}