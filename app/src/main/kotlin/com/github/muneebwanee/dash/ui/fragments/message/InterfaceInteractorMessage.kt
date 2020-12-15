package com.github.muneebwanee.dash.ui.fragments.message

import com.github.muneebwanee.dash.di.PerActivity
import com.github.muneebwanee.dash.ui.activities.base.InterfaceInteractor

/**
 * Created by muneebwanee on 15/12/20.
 */
@PerActivity
interface InterfaceInteractorMessage<V : InterfaceViewMessage> : InterfaceInteractor<V>