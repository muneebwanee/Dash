package com.github.muneebwanee.dash.ui.fragments.calls

import com.github.muneebwanee.dash.ui.activities.base.InterfaceView
import com.github.muneebwanee.dash.ui.adapters.callsadapter.CallsRecyclerAdapter

/**
 * Created by muneebwanee on 15/12/20.
 */
interface InterfaceViewCalls : InterfaceView {

    fun setRecyclerAdapter(recyclerAdapter: CallsRecyclerAdapter)

}