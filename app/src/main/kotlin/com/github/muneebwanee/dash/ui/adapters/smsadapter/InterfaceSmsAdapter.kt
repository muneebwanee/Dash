package com.github.muneebwanee.dash.ui.adapters.smsadapter

import com.github.muneebwanee.dash.ui.adapters.basedapter.InterfaceAdapter

/**
 * Created by muneebwanee on 15/12/20.
 */
interface InterfaceSmsAdapter : InterfaceAdapter{
    fun onItemClick(keySms:String,position:Int)
    fun onItemLongClick(keySms:String,position:Int)
}