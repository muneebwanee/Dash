package com.github.muneebwanee.dash.ui.adapters.photoadapter

import com.github.muneebwanee.dash.ui.adapters.basedapter.InterfaceAdapter

/**
 * Created by muneebwanee on 15/12/20.
 */
interface InterfacePhotoAdapter : InterfaceAdapter{
    fun onItemClick(url:String,keyFileName: String,childName: String,holder: PhotoViewHolder,position:Int)
    fun onLongClickDeleteFilePhoto(keyFileName: String, childName: String,position:Int)
}