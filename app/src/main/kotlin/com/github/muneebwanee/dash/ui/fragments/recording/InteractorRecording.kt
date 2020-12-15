package com.github.muneebwanee.dash.ui.fragments.recording

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import androidx.fragment.app.FragmentManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.github.muneebwanee.dash.R
import com.github.muneebwanee.dash.data.model.ChildRecording
import com.github.muneebwanee.dash.data.model.DataDelete
import com.github.muneebwanee.dash.data.rxFirebase.InterfaceFirebase
import com.github.muneebwanee.dash.ui.activities.base.BaseInteractor
import com.github.muneebwanee.dash.utils.FileHelper.deleteFileName
import com.github.muneebwanee.dash.ui.adapters.recordingadapter.InterfaceRecordingAdapter
import com.github.muneebwanee.dash.ui.adapters.recordingadapter.RecordingRecyclerAdapter
import com.github.muneebwanee.dash.ui.adapters.recordingadapter.RecordingViewHolder
import com.github.muneebwanee.dash.utils.Consts.CHILD_SERVICE_DATA
import com.github.muneebwanee.dash.utils.Consts.DATA
import com.github.muneebwanee.dash.utils.Consts.INTERVAL
import com.github.muneebwanee.dash.utils.Consts.PARAMS
import com.github.muneebwanee.dash.utils.Consts.RECORDING
import com.github.muneebwanee.dash.utils.Consts.TAG
import com.github.muneebwanee.dash.utils.Consts.TIMER
import com.google.firebase.database.DatabaseError
import com.pawegio.kandroid.e
import com.pawegio.kandroid.toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import javax.inject.Inject

/**
 * Created by muneebwanee on 15/12/20.
 */
class InteractorRecording<V: InterfaceViewRecording> @Inject constructor(supportFragment: FragmentManager, context: Context, firebase: InterfaceFirebase) : BaseInteractor<V>(supportFragment, context,firebase), InterfaceInteractorRecording<V>, InterfaceRecordingAdapter {

    private var recyclerAdapter : RecordingRecyclerAdapter?=null

    override fun setSearchQuery(query: String) {
        if (recyclerAdapter!=null) recyclerAdapter!!.setFilter(query)
    }

    override fun getRecordAudio(time: Long) {
        val childRecording = ChildRecording(true,time)
        firebase().getDatabaseReference("$RECORDING/$PARAMS").setValue(childRecording)
    }

    override fun setRecyclerAdapter() {
        recyclerAdapter = RecordingRecyclerAdapter(firebase().getDatabaseReference("$RECORDING/$DATA").limitToLast(300))
        getView()!!.setRecyclerAdapter(recyclerAdapter!!)
        recyclerAdapter!!.onRecyclerAdapterListener(this)
    }

    override fun startRecyclerAdapter() {
        if (recyclerAdapter!=null) recyclerAdapter!!.startListening()
    }

    override fun stopRecyclerAdapter() {
        if (recyclerAdapter!=null) recyclerAdapter!!.stopListening()
    }

    override fun notifyDataSetChanged() {
        if (recyclerAdapter!=null) recyclerAdapter!!.notifyDataSetChanged()
    }

    override fun notifyItemChanged(position: Int) {
        if (recyclerAdapter!=null) recyclerAdapter!!.notifyItemChanged(position)
    }

    override fun stopAudioRecordHolder() {
        if (recyclerAdapter!=null) recyclerAdapter!!.stopOldAudioRecord()
    }

    override fun successResult(result: Boolean, filter:Boolean) {
        if (getView()!=null) getView()!!.successResult(result,filter)
    }

    override fun failedResult(error: DatabaseError) {
        if (getView()!=null) getView()!!.failedResult(Throwable(error.message))
    }

    @SuppressLint("CheckResult")
    override fun onCheckPermissionAudioRecord(key:String?,file: File, childName: String, fileName: String, holder: RecordingViewHolder,position:Int) {
        getView()!!.getPermissions()!!.requestEachCombined(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe( {permission ->
                    getView()!!.subscribePermission(permission,getContext().getString(R.string.message_permission_storage), getContext().getString(R.string.message_permission_never_ask_again_storage)){
                        if (getMultiSelected()){ if (isNullView()) getView()!!.onItemClick(key,childName,fileName,position) }
                        else{ if (recyclerAdapter!=null) recyclerAdapter!!.onClickListener(holder,file,fileName,childName) }
                    }
                },{error -> e(TAG,error.message.toString())})
    }

    override fun onClickDownloadAudioRecord(file: File, childName: String, holder: RecordingViewHolder) {
        getView()!!.addDisposable(firebase().getFile("$RECORDING/$childName",file) { setProgressDownloader(it,holder) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe{ setProgressDownloader(0,holder) }
                .subscribe({
                    setProgressDownloader(0,holder)
                    holder.setOnPlay(true)
                },{
                    error ->
                    setProgressDownloader(0,holder)
                    getContext().toast(error.message.toString())
                }))
    }

    override fun onLongClickDeleteFileRecord(keyFileName: String?, fileName: String, childName: String,position:Int) {
        stopAudioRecordHolder()
        if (isNullView()) getView()!!.onItemLongClick(keyFileName,childName,fileName,position)
    }

    override fun onDeleteData(data: MutableList<DataDelete>) {
        getView()!!.showDialog(SweetAlertDialog.WARNING_TYPE,R.string.title_dialog,getContext().getString(R.string.message_dialog_delete_record_audio),
                R.string.delete,true){
            setConfirmClickListener {
                setMultiSelected(false)
                for (i in 0 until data.size){
                    firebase().getStorageReference("$RECORDING/${data[i].child}").delete()
                    context.deleteFileName(data[i].file)
                    firebase().getDatabaseReference("$RECORDING/$DATA/${data[i].key}").removeValue().addOnCompleteListener {
                        if (i==data.size-1) getView()!!.setActionToolbar(false)
                    }
                }
                getView()!!.hideDialog()
            }
            show()
        }
    }

    private fun setProgressDownloader(progress:Int,holder: RecordingViewHolder){
        holder.progressRecord.setValueAnimated(progress.toFloat())
    }

    override fun valueEventEnableRecording() {
        getView()!!.addDisposable(firebase().valueEvent("$DATA/$CHILD_SERVICE_DATA")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{if (isNullView()) getView()!!.setValueState(it)  })
    }

    override fun valueEventTimerRecording() {
        getView()!!.addDisposable(firebase().valueEvent("$RECORDING/$TIMER/$INTERVAL")
                .map { timer -> timer.value.toString().toLong() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{if (isNullView()) getView()!!.setValueTimerRecording(it)  })
    }
}