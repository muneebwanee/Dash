package com.github.muneebwanee.dash.ui.adapters.recordingadapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import com.github.muneebwanee.dash.R
import com.github.muneebwanee.dash.data.model.Recording
import com.github.muneebwanee.dash.ui.adapters.basedapter.BaseAdapter
import com.github.muneebwanee.dash.utils.ConstFun.firebaseOptions
import com.github.muneebwanee.dash.utils.Consts.ADDRESS_AUDIO_RECORD
import com.github.muneebwanee.dash.utils.Consts.TAG
import com.github.muneebwanee.dash.utils.FileHelper.getFileNameAudio
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
class RecordingRecyclerAdapter(private val query: Query) : BaseAdapter<Recording,RecordingViewHolder>(firebaseOptions(query)){

    private lateinit var interfaceRecordAdapter: InterfaceRecordingAdapter
    private var holder: RecordingViewHolder? = null

    fun setFilter(filter:String){
        startFilter()
        if (filter=="") updateOptions(firebaseOptions(query))
        else updateOptions(firebaseOptions(query,filter,"dateTime"))
    }

    override fun startFilter() = interfaceRecordAdapter.successResult(result = false, filter = true)

    override fun onDataChanged() {
        if (getSnapshots().size == 0) interfaceRecordAdapter.successResult(false)
        else interfaceRecordAdapter.successResult(true)
    }

    override fun onError(e: DatabaseError) {
        interfaceRecordAdapter.failedResult(e)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordingViewHolder =
            RecordingViewHolder(parent.context.inflateLayout(R.layout.item_recordings, parent, false), parent.context)

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: RecordingViewHolder, position: Int, model: Recording) {

        holder.bind(getItem(position))

        val key = getRef(position).key
        var fileName = holder.itemView.context.getFileNameAudio(model.nameAudio, model.dateTime)
        val childName = fileName.replace("${holder.itemView.context.getFilePath()}/$ADDRESS_AUDIO_RECORD/", "")
        fileName = fileName.replace(":", "")
        val file = File(fileName)

        holder.isSelectedItem(key!!,file)

        RxView.clicks(holder.itemClick).subscribe({
            interfaceRecordAdapter.onCheckPermissionAudioRecord(key,file, childName, fileName, holder,position)
        },{ e(TAG, it.message.toString()) })

        RxView.clicks(holder.btnPlay).subscribe({
            interfaceRecordAdapter.onCheckPermissionAudioRecord(key,file, childName, fileName, holder,position)
        },{ e(TAG, it.message.toString()) })

        RxView.longClicks(holder.itemClick).subscribe({
            interfaceRecordAdapter.onLongClickDeleteFileRecord(key, fileName, childName,position)
        }, { e(TAG, it.message.toString()) })

    }

    fun onClickListener(holder: RecordingViewHolder, file: File, fileName: String, childName: String) {
        if (file.exists()) {
            if (holder.getPlaying()) holder.onPauseAudioRecord()
            else {
                if (holder.getStopPlayer()) {
                    stopOldAudioRecord()
                    this.holder = holder
                    holder.initializeMediaPlayer(fileName)
                }
                holder.onPlayAudioRecord()
            }
        } else {
            if (!holder.downloader){
                holder.downloader = true
                interfaceRecordAdapter.onClickDownloadAudioRecord(file, childName, holder)
            }
        }
    }

    fun onRecyclerAdapterListener(interfaceRecordAdapter: InterfaceRecordingAdapter) {
        this.interfaceRecordAdapter = interfaceRecordAdapter
    }

    fun stopOldAudioRecord() {
        if (holder != null) holder!!.onStopAudioRecord()
    }


}