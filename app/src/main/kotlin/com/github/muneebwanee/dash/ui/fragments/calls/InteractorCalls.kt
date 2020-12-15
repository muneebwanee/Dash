package com.github.muneebwanee.dash.ui.fragments.calls

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import androidx.fragment.app.FragmentManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.github.muneebwanee.dash.R
import com.github.muneebwanee.dash.data.model.DataDelete
import com.github.muneebwanee.dash.data.rxFirebase.InterfaceFirebase
import com.github.muneebwanee.dash.ui.activities.base.BaseInteractor
import com.github.muneebwanee.dash.ui.adapters.callsadapter.CallsRecyclerAdapter
import com.github.muneebwanee.dash.ui.adapters.callsadapter.CallsViewHolder
import com.github.muneebwanee.dash.ui.adapters.callsadapter.InterfaceCallsAdapter
import com.github.muneebwanee.dash.utils.FileHelper.deleteFileName
import com.github.muneebwanee.dash.utils.Consts.CALLS
import com.github.muneebwanee.dash.utils.Consts.DATA
import com.github.muneebwanee.dash.utils.Consts.TAG
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
class InteractorCalls<V: InterfaceViewCalls> @Inject constructor(supportFragment: FragmentManager, context: Context, firebase: InterfaceFirebase) : BaseInteractor<V>(supportFragment, context,firebase), InterfaceInteractorCalls<V>, InterfaceCallsAdapter {

    private var recyclerAdapter:CallsRecyclerAdapter?=null

    override fun setSearchQuery(query: String) {
        if (recyclerAdapter!=null)recyclerAdapter!!.setFilter(query)
    }

    override fun setRecyclerAdapter() {
        recyclerAdapter = CallsRecyclerAdapter(firebase().getDatabaseReference("$CALLS/$DATA").limitToLast(300))
        getView()!!.setRecyclerAdapter(recyclerAdapter!!)
        recyclerAdapter!!.onRecyclerAdapterListener(this)
    }

    override fun startRecyclerAdapter() {
        if (recyclerAdapter!=null) recyclerAdapter!!.startListening()
    }

    override fun stopRecyclerAdapter() {
        if (recyclerAdapter!=null) recyclerAdapter!!.stopListening()
    }

    override fun stopAudioCallHolder() {
        if (recyclerAdapter!=null) recyclerAdapter!!.stopOldAudioCall()
    }

    override fun notifyDataSetChanged() {
        if (recyclerAdapter!=null) recyclerAdapter!!.notifyDataSetChanged()
    }

    override fun notifyItemChanged(position: Int) {
        if (recyclerAdapter!=null) recyclerAdapter!!.notifyItemChanged(position)
    }

    override fun successResult(result: Boolean, filter:Boolean) {
        if (getView()!=null) getView()!!.successResult(result,filter)
    }

    override fun failedResult(error: DatabaseError) {
        if (getView()!=null) getView()!!.failedResult(Throwable(error.message))
    }

    @SuppressLint("CheckResult")
    override fun onCheckPermissionAudioCalls(key:String,file: File, childName: String, fileName: String, holder: CallsViewHolder,position:Int) {
        getView()!!.getPermissions()!!.requestEachCombined(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe ({permission ->
                    getView()!!.subscribePermission(permission,getContext().getString(R.string.message_permission_storage), getContext().getString(R.string.message_permission_never_ask_again_storage)){
                        if (getMultiSelected()) { if (isNullView()) getView()!!.onItemClick(key,childName,fileName,position) }
                        else { if (recyclerAdapter!=null) recyclerAdapter!!.onClickListener(holder,file,fileName,childName) }
                    }
                },{error -> e(TAG,error.message.toString())})
    }

    override fun onClickDownloadAudioCall(file: File, childName: String, holder: CallsViewHolder) {
        getView()!!.addDisposable(firebase().getFile("$CALLS/$childName",file){ setProgressDownloader(it,holder) }
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

    override fun onLongClickDeleteFileCall(keyFileName:String,fileName: String,childName: String,position:Int) {
        stopAudioCallHolder()
        if (isNullView()) getView()!!.onItemLongClick(keyFileName,childName,fileName,position)
    }

    override fun onDeleteData(data: MutableList<DataDelete>) {
        getView()!!.showDialog(SweetAlertDialog.WARNING_TYPE,R.string.title_dialog,getContext().getString(R.string.message_dialog_delete_call_audio),
                R.string.delete,true){
            setConfirmClickListener {
                setMultiSelected(false)
                for (i in 0 until data.size){
                    firebase().getStorageReference("$CALLS/${data[i].child}").delete()
                    context.deleteFileName(data[i].file)
                    firebase().getDatabaseReference("$CALLS/$DATA/${data[i].key}").removeValue().addOnCompleteListener {
                        if (i==data.size-1) getView()!!.setActionToolbar(false)
                    }
                }
                getView()!!.hideDialog()
            }
            show()
        }
    }

    private fun setProgressDownloader(progress:Int,holder: CallsViewHolder){
        holder.progressCall.setValueAnimated(progress.toFloat())
    }

}