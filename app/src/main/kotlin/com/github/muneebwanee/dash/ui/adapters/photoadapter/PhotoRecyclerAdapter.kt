package com.github.muneebwanee.dash.ui.adapters.photoadapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import com.github.muneebwanee.dash.R
import com.github.muneebwanee.dash.data.model.Photo
import com.github.muneebwanee.dash.ui.adapters.basedapter.BaseAdapter
import com.github.muneebwanee.dash.utils.ConstFun.firebaseOptions
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.pawegio.kandroid.inflateLayout
import com.github.muneebwanee.dash.utils.Consts.TAG
import com.jakewharton.rxbinding2.view.RxView
import com.pawegio.kandroid.e

/**
 * Created by muneebwanee on 15/12/20.
 */
class PhotoRecyclerAdapter(private val query: Query) : BaseAdapter<Photo, PhotoViewHolder>(firebaseOptions(query)) {

    private lateinit var interfacePhotoAdapter: InterfacePhotoAdapter

    fun setFilter(filter:String){
        startFilter()
        if (filter=="") updateOptions(firebaseOptions(query))
        else updateOptions(firebaseOptions(query,filter,"dateTime"))
    }

    override fun startFilter() = interfacePhotoAdapter.successResult(result = false, filter = true)

    override fun onDataChanged() = if (getSnapshots().size == 0) interfacePhotoAdapter.successResult(false)
    else interfacePhotoAdapter.successResult(true)

    override fun onError(e: DatabaseError) {
        interfacePhotoAdapter.failedResult(e)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder =
            PhotoViewHolder(parent.context.inflateLayout(R.layout.item_photo, parent, false))

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int, model: Photo) {
        holder.bind(getItem(position))
        val key = getRef(position).key

        holder.isSelected(key!!)

        RxView.clicks(holder.itemClick).subscribe({
            interfacePhotoAdapter.onItemClick(model.urlPhoto!!,key,model.nameRandom!!,holder,position)
        },{ e(TAG, it.message.toString()) })

        RxView.longClicks(holder.itemClick).subscribe({
            interfacePhotoAdapter.onLongClickDeleteFilePhoto(key, model.nameRandom!!,position)
        }, { e(TAG, it.message.toString()) })

    }

    fun onRecyclerAdapterListener(interfacePhotoAdapter: InterfacePhotoAdapter) {
        this.interfacePhotoAdapter = interfacePhotoAdapter
    }

}