package com.github.muneebwanee.dash.ui.fragments.social

import com.github.muneebwanee.dash.ui.activities.base.InterfaceView
import com.google.firebase.database.DataSnapshot

/**
 * Created by muneebwanee on 15/12/20.
 */
interface InterfaceViewSocial : InterfaceView {

    fun setValuePermission(dataSnapshot: DataSnapshot)
    fun successResult(dataSnapshot: DataSnapshot)

}