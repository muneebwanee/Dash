package com.github.muneebwanee.dash.ui.adapters.callsadapter

import com.github.muneebwanee.dash.ui.adapters.basedapter.InterfaceAdapter
import java.io.File

/**
 * Created by muneebwanee on 15/12/20.
 */
interface InterfaceCallsAdapter : InterfaceAdapter {
    fun onCheckPermissionAudioCalls(key:String,file: File, childName: String, fileName: String, holder: CallsViewHolder,position:Int)
    fun onClickDownloadAudioCall(file: File, childName: String, holder: CallsViewHolder)
    fun onLongClickDeleteFileCall(keyFileName: String, fileName: String, childName: String,position:Int)
}