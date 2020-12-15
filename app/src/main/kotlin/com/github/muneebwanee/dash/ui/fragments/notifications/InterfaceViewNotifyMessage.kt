package com.github.muneebwanee.dash.ui.fragments.notifications

import com.github.muneebwanee.dash.ui.activities.base.InterfaceView
import com.github.muneebwanee.dash.ui.adapters.notifyadapter.NotifyMessageRecyclerAdapter
import com.google.firebase.database.DataSnapshot

/**
 * Created by muneebwanee on 15/12/20.
 */
interface InterfaceViewNotifyMessage : InterfaceView {

    fun setRecyclerAdapter(recyclerAdapter: NotifyMessageRecyclerAdapter)
    fun setValueState(dataSnapshot: DataSnapshot)

}