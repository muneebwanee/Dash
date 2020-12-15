package com.github.muneebwanee.dash.ui.adapters.notifyadapter

import com.github.muneebwanee.dash.ui.adapters.basedapter.InterfaceAdapter


/**
 * Created by muneebwanee on 15/12/20.
 */
interface InterfaceNotifyMessageAdapter : InterfaceAdapter{
    fun onItemClick(key: String?, child: String,position:Int)
    fun onItemLongClick(key: String?, child: String,position:Int)
}