package com.github.muneebwanee.dash.ui.adapters.notifyadapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import com.github.muneebwanee.dash.R
import com.github.muneebwanee.dash.data.model.NotificationMessages
import com.github.muneebwanee.dash.ui.adapters.basedapter.BaseAdapter
import com.github.muneebwanee.dash.utils.ConstFun.firebaseOptions
import com.github.muneebwanee.dash.utils.Consts
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.jakewharton.rxbinding2.view.RxView
import com.pawegio.kandroid.e
import com.pawegio.kandroid.inflateLayout

class NotifyMessageRecyclerAdapter(private val query:Query,private val interfaceNotifyMessageAdapter:InterfaceNotifyMessageAdapter) : BaseAdapter<NotificationMessages,NotifyMessageViewHolder>(firebaseOptions(query)) {

    fun setFilter(filter:String){
        startFilter()
        if (filter=="") updateOptions(firebaseOptions(query))
        else updateOptions(firebaseOptions(query,filter,"text","title"))
    }

    override fun startFilter() = interfaceNotifyMessageAdapter.successResult(result = false, filter = true)

    override fun onDataChanged() = if (getSnapshots().size == 0) interfaceNotifyMessageAdapter.successResult(false)
    else interfaceNotifyMessageAdapter.successResult(true)

    override fun onError(e: DatabaseError) = interfaceNotifyMessageAdapter.failedResult(e)

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): NotifyMessageViewHolder =
            NotifyMessageViewHolder(p0.context.inflateLayout(R.layout.item_notify_message))

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: NotifyMessageViewHolder, position: Int, model: NotificationMessages) {
        val key = getRef(position).key
        holder.bind(model)

        holder.isSelectedItem(key!!,model.urlImage)

        RxView.clicks(holder.itemClick).subscribe({
            interfaceNotifyMessageAdapter.onItemClick(key,model.nameImage!!,position)
        },{ e(Consts.TAG,it.message.toString()) })

        RxView.longClicks(holder.itemClick).subscribe({
            interfaceNotifyMessageAdapter.onItemLongClick(key,model.nameImage!!,position)
        },{ e(Consts.TAG,it.message.toString()) })
    }

}