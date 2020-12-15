package com.github.muneebwanee.dash.ui.fragments.notifications

import android.content.Context
import androidx.fragment.app.FragmentManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.github.muneebwanee.dash.R
import com.github.muneebwanee.dash.data.model.DataDelete
import com.github.muneebwanee.dash.data.rxFirebase.InterfaceFirebase
import com.github.muneebwanee.dash.ui.activities.base.BaseInteractor
import com.github.muneebwanee.dash.ui.adapters.notifyadapter.InterfaceNotifyMessageAdapter
import com.github.muneebwanee.dash.ui.adapters.notifyadapter.NotifyMessageRecyclerAdapter
import com.github.muneebwanee.dash.utils.Consts.CHILD_PERMISSION
import com.github.muneebwanee.dash.utils.Consts.DATA
import com.github.muneebwanee.dash.utils.Consts.NOTIFICATION_MESSAGE
import com.google.firebase.database.DatabaseError
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by muneebwanee on 15/12/20.
 */
class InteractorNotifyMessage<V : InterfaceViewNotifyMessage> @Inject constructor(supportFragment: FragmentManager, context: Context, firebase: InterfaceFirebase) : BaseInteractor<V>(supportFragment, context, firebase), InterfaceInteractorNotifyMessage<V>, InterfaceNotifyMessageAdapter {

    private var recyclerAdapter : NotifyMessageRecyclerAdapter ?=null

    override fun setSearchQuery(query: String) {
        if (recyclerAdapter!=null) recyclerAdapter!!.setFilter(query)
    }

    override fun setRecyclerAdapter() {
        recyclerAdapter = NotifyMessageRecyclerAdapter(firebase().getDatabaseReference("$NOTIFICATION_MESSAGE/$DATA").limitToLast(300),this)
        if (isNullView()) getView()!!.setRecyclerAdapter(recyclerAdapter!!)
    }

    override fun notifyDataSetChanged() {
        if (recyclerAdapter!=null) recyclerAdapter!!.notifyDataSetChanged()
    }

    override fun notifyItemChanged(position: Int) {
        if (recyclerAdapter!=null) recyclerAdapter!!.notifyItemChanged(position)
    }

    override fun onItemClick(key: String?, child: String,position:Int) {
        if (getMultiSelected()) if (isNullView()) getView()!!.onItemClick(key,child,"",position)
    }

    override fun onItemLongClick(key: String?, child: String,position:Int) {
        if (isNullView()) getView()!!.onItemLongClick(key,child,"",position)
    }

    override fun startRecyclerAdapter() {
        if (recyclerAdapter!=null) recyclerAdapter!!.startListening()
    }

    override fun stopRecyclerAdapter() {
        if (recyclerAdapter!=null) recyclerAdapter!!.stopListening()
    }

    override fun successResult(result: Boolean, filter:Boolean) {
        if (isNullView()) getView()!!.successResult(result,filter)
    }

    override fun failedResult(error: DatabaseError) {
        if (isNullView()) getView()!!.failedResult(Throwable(error.message))
    }

    override fun onDeleteData(data: MutableList<DataDelete>) {
        getView()!!.showDialog(SweetAlertDialog.WARNING_TYPE, R.string.title_dialog,getContext().getString(R.string.message_dialog_delete_notify),R.string.delete,true){
            setConfirmClickListener {
                setMultiSelected(false)
                for (i in 0 until data.size){
                    firebase().getStorageReference("$NOTIFICATION_MESSAGE/${data[i].child}").delete()
                    firebase().getDatabaseReference("$NOTIFICATION_MESSAGE/$DATA/${data[i].key}").removeValue().addOnCompleteListener {
                        if (i==data.size-1) getView()!!.setActionToolbar(false)
                    }
                }
                getView()!!.hideDialog()
            }
            show()
        }
    }

    override fun valueEventEnableNotifications() {
        getView()!!.addDisposable(firebase().valueEvent("$NOTIFICATION_MESSAGE/$CHILD_PERMISSION")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { if (getView() != null) getView()!!.setValueState(it) })
    }

}