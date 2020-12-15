package com.github.muneebwanee.dash.ui.adapters.callsadapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import com.github.muneebwanee.dash.R
import com.github.muneebwanee.dash.data.model.Calls
import com.github.muneebwanee.dash.ui.adapters.basedapter.BaseAdapter
import com.github.muneebwanee.dash.utils.ConstFun.firebaseOptions
import com.github.muneebwanee.dash.utils.Consts.ADDRESS_AUDIO_CALLS
import com.github.muneebwanee.dash.utils.Consts.TAG
import com.github.muneebwanee.dash.utils.FileHelper.getFileNameCall
import com.github.muneebwanee.dash.utils.FileHelper.getFilePath
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.jakewharton.rxbinding2.view.RxView
import com.pawegio.kandroid.e
import com.pawegio.kandroid.inflateLayout
import java.io.File

/**
 * Created by muneebwanee on 15/12/20.
 */
class CallsRecyclerAdapter(private val query: Query) : BaseAdapter<Calls, CallsViewHolder>(firebaseOptions(query)) {

    private lateinit var interfaceCallsAdapter: InterfaceCallsAdapter
    private var holder: CallsViewHolder? = null

    fun setFilter(filter:String){
        startFilter()
        if (filter=="") updateOptions(firebaseOptions(query))
        else updateOptions(firebaseOptions(query,filter,"contact","phoneNumber"))
    }

    override fun startFilter() {
        interfaceCallsAdapter.successResult(result = false, filter = true)
    }

    override fun onDataChanged() {
        if (getSnapshots().size == 0) interfaceCallsAdapter.successResult(false)
        else interfaceCallsAdapter.successResult(true)
    }

    override fun onError(e: DatabaseError) {
        interfaceCallsAdapter.failedResult(e)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallsViewHolder =
            CallsViewHolder(parent.context.inflateLayout(R.layout.item_calls, parent, false), parent.context)

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: CallsViewHolder, position: Int, model: Calls) {
        holder.bind(getItem(position))

        val key = getRef(position).key

        var fileName = holder.itemView.context.getFileNameCall(model.phoneNumber, model.dateTime)
        val childName = fileName.replace("${holder.itemView.context.getFilePath()}/$ADDRESS_AUDIO_CALLS/", "")
        fileName = fileName.replace(":", "")
        val file = File(fileName)

        holder.isSelectedItem(key!!,file)

        RxView.clicks(holder.itemClick).subscribe({
            interfaceCallsAdapter.onCheckPermissionAudioCalls(key,file, childName, fileName, holder,position)
        },{ e(TAG, it.message.toString()) })

        RxView.clicks(holder.btnPlay).subscribe({
            interfaceCallsAdapter.onCheckPermissionAudioCalls(key,file, childName, fileName, holder,position)
        },{ e(TAG, it.message.toString()) })

        RxView.longClicks(holder.itemClick).subscribe({
            interfaceCallsAdapter.onLongClickDeleteFileCall(key, fileName, childName,position)
        }, { e(TAG, it.message.toString()) })

    }

    fun onClickListener(holder: CallsViewHolder, file: File, fileName: String, childName: String) {
        if (file.exists()) {
            if (holder.getPlaying()) holder.onPauseAudioCall()
            else {
                if (holder.getStopPlayer()) {
                    stopOldAudioCall()
                    this.holder = holder
                    holder.initializeMediaPlayer(fileName)
                }
                holder.onPlayAudioCall()
            }
        } else {
            if (!holder.downloader){
                holder.downloader = true
                interfaceCallsAdapter.onClickDownloadAudioCall(file, childName, holder)
            }
        }
    }

    fun onRecyclerAdapterListener(interfaceCallsAdapter: InterfaceCallsAdapter) {
        this.interfaceCallsAdapter = interfaceCallsAdapter
    }

    fun stopOldAudioCall() {
        if (holder != null) holder!!.onStopAudioCall()
    }

}