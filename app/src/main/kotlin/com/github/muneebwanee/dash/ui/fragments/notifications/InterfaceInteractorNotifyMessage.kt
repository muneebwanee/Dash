package com.github.muneebwanee.dash.ui.fragments.notifications

import com.github.muneebwanee.dash.ui.activities.base.InterfaceInteractor

/**
 * Created by muneebwanee on 15/12/20.
 */
interface InterfaceInteractorNotifyMessage<V : InterfaceViewNotifyMessage> : InterfaceInteractor<V> {
    fun valueEventEnableNotifications()
}