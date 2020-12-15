package com.github.muneebwanee.dash.ui.fragments.message

import com.github.muneebwanee.dash.ui.activities.base.InterfaceView
import com.github.muneebwanee.dash.ui.adapters.smsadapter.SmsRecyclerAdapter

/**
 * Created by muneebwanee on 15/12/20.
 */
interface InterfaceViewMessage : InterfaceView {

    fun setRecyclerAdapter(smsRecyclerAdapter: SmsRecyclerAdapter)

}