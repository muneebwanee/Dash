package com.github.muneebwanee.dash.ui.fragments.keylog

import com.github.muneebwanee.dash.ui.activities.base.InterfaceView
import com.github.muneebwanee.dash.ui.adapters.keysadapter.KeysRecyclerAdapter
import com.google.firebase.database.DataSnapshot

/**
 * Created by muneebwanee on 15/12/20.
 */
interface InterfaceViewKeys : InterfaceView {

    fun setValueState(dataSnapshot: DataSnapshot)
    fun setRecyclerAdapter(recyclerAdapter: KeysRecyclerAdapter)

}