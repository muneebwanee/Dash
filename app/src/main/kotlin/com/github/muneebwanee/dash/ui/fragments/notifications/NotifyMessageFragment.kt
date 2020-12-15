package com.github.muneebwanee.dash.ui.fragments.notifications

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.appbar.AppBarLayout
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.github.clans.fab.FloatingActionButton
import com.github.muneebwanee.dash.R
import com.github.muneebwanee.dash.data.model.DataDelete
import com.github.muneebwanee.dash.data.preference.DataSharePreference.setSelectedItem
import com.github.muneebwanee.dash.data.preference.DataSharePreference.clearSelectedItem
import com.github.muneebwanee.dash.ui.fragments.base.BaseFragment
import com.github.muneebwanee.dash.ui.adapters.notifyadapter.NotifyMessageRecyclerAdapter
import com.github.muneebwanee.dash.ui.animation.OvershootInRightAnimator
import com.github.muneebwanee.dash.ui.widget.CustomRecyclerView
import com.github.muneebwanee.dash.ui.widget.OnScrollListener
import com.github.muneebwanee.dash.ui.widget.toolbar.CustomToolbar
import com.github.muneebwanee.dash.utils.ConstFun.contentGlobalLayout
import com.github.muneebwanee.dash.utils.ConstFun.isScrollToolbar
import com.google.firebase.database.DataSnapshot
import com.pawegio.kandroid.*
import kotterknife.bindView
import javax.inject.Inject

/**
 * Created by muneebwanee on 15/12/20.
 */
class NotifyMessageFragment : BaseFragment(R.layout.fragment_notifications) , InterfaceViewNotifyMessage {

    companion object { const val TAG = "NotifyMessageFragment" }

    private var dataList : MutableList<DataDelete> = mutableListOf()

    private val viewProgress : LinearLayout by bindView(R.id.progress_placeholder)
    private val viewNotHave : LinearLayout by bindView(R.id.not_have_placeholder)
    private val viewFailed : LinearLayout by bindView(R.id.failed_placeholder)
    private val txtNotHave : TextView by bindView(R.id.txt_not_have_get)
    private val txtFailed  : TextView by bindView(R.id.txt_failed_get)
    private val list : CustomRecyclerView by bindView(R.id.list)
    private val floatingButton : FloatingActionButton by bindView(R.id.floating_button)
    private val content : ConstraintLayout by bindView(R.id.content)
    private val appBar : AppBarLayout by bindView(R.id.app_bar)
    private val toolbar : CustomToolbar by bindView(R.id.toolbar)
    private val main : CoordinatorLayout by bindView(R.id.main_view)

    private var recyclerAdapter : NotifyMessageRecyclerAdapter?=null

    @Inject lateinit var interactor : InterfaceInteractorNotifyMessage<InterfaceViewNotifyMessage>

    @Inject lateinit var layoutM : LinearLayoutManager

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setToolbar(toolbar,true,R.string.search_notify,R.id.nav_clear_notifications)
        contentGlobalLayout(content, appBar)
        list.setAppBar(appBar)
        if (getComponent()!=null){
            getComponent()!!.inject(this)
            interactor.onAttach(this)
            startData()
            timeConnection()
        }
    }


    private fun timeConnection(){
        runDelayedOnUiThread(13000){
            if (viewProgress.isShown) failedResult(Throwable(getString(R.string.error_database_connection)))
        }
    }

    private fun startData(){
        interactor.setRecyclerAdapter()
        interactor.valueEventEnableNotifications()
    }

    override fun onStart() {
        super.onStart()
        interactor.startRecyclerAdapter()
    }

    override fun setRecyclerAdapter(recyclerAdapter: NotifyMessageRecyclerAdapter) {
        this.recyclerAdapter = recyclerAdapter
        layoutM.stackFromEnd = true
        layoutM.reverseLayout = true
        list.apply {
            itemAnimator = OvershootInRightAnimator()
            itemAnimator?.addDuration = 600
            itemAnimator?.removeDuration = 600
            layoutManager = layoutM
            adapter = recyclerAdapter
            recycledViewPool.clear()
            addOnScrollListener(OnScrollListener(floatingButton,layoutM))
        }
        floatingButton.setOnClickListener {
            recyclerPosition()
        }
    }

    private fun recyclerPosition(){
        appBar.setExpanded(true)
        if (recyclerAdapter!=null) list.scrollToPosition(recyclerAdapter!!.itemCount-1)}

    override fun onSearchConfirmed(text: CharSequence) = interactor.setSearchQuery(text.toString())

    override fun onButtonClicked(buttonCode: Int) {
        when(buttonCode){
            CustomToolbar.BUTTON_BACK -> interactor.setSearchQuery("")
            CustomToolbar.BUTTON_ACTION_DELETE -> interactor.onDeleteData(dataList)
            CustomToolbar.BUTTON_STATE -> showSnackbar(if (toolbar.statePermission) R.string.enable_notification_message else R.string.disable_notification_message,main)
            CustomToolbar.BUTTON_CHILD_USER -> changeChild(TAG)
            else -> super.onButtonClicked(buttonCode)
        }
    }

    override fun onItemClick(key: String?, child: String,file: String,position:Int) {
        itemSelected(key,child)
    }

    override fun onItemLongClick(key: String?, child: String,file: String,position:Int) {
        if (!interactor.getMultiSelected()){
            interactor.setMultiSelected(true)
            setActionToolbar(true)
        }
        itemSelected(key,child)
    }

    private fun itemSelected(key: String?,child: String){
        if (!key.isNullOrEmpty()){
            val data = DataDelete(key,child,"")
            if (dataList.contains(data)) {
                dataList.remove(data)
                context!!.setSelectedItem(key,false)
            }else {
                dataList.add(data)
                context!!.setSelectedItem(key, true)
            }

            if (dataList.isNotEmpty()) toolbar.setTitle = "${dataList.size} ${getString(R.string.selected)}"
            else setActionToolbar(false)

            interactor.notifyDataSetChanged()
        }
    }

    override fun onActionStateChanged(enabled: Boolean) {
        if (!enabled){
            dataList = mutableListOf()
            appBar.setExpanded(true)
            context!!.clearSelectedItem()
            if (interactor.getMultiSelected()){
                interactor.setMultiSelected(false)
                interactor.notifyDataSetChanged()
            }
        }
        super.onActionStateChanged(enabled)
    }

    override fun setValueState(dataSnapshot: DataSnapshot) {
        toolbar.enableStatePermission = true
        try {
            if (dataSnapshot.exists()) toolbar.statePermission = dataSnapshot.value as Boolean
            else toolbar.statePermission = false
        }catch (t:Throwable){
            e(TAG,t.message.toString())
        }
    }

    override fun successResult(result: Boolean, filter:Boolean) {
        isScrollToolbar(toolbar,result)
        if (result){
            viewProgress.hide()
            viewNotHave.hide()
            viewFailed.hide()
            list.show()
            recyclerPosition()
        }else{
            floatingButton.hide(true)
            viewFailed.hide()
            list.hide()
            appBar.setExpanded(true)
            if (filter) { viewProgress.show() ; viewNotHave.hide() }
            else{
                viewProgress.hide()
                viewNotHave.show()
                txtNotHave.text = getString(R.string.not_have_notifications_text)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun failedResult(throwable: Throwable) {
        floatingButton.hide(true)
        viewProgress.hide()
        viewNotHave.hide()
        list.hide()
        isScrollToolbar(toolbar,false)
        appBar.setExpanded(true)
        viewFailed.show()
        txtFailed.text = "${getString(R.string.failed_notifications_text)}, ${throwable.message}"
    }

    override fun onBackPressed(): Boolean {
        return when {
            toolbar.isSearchEnabled -> { toolbar.disableSearch() ; true }
            toolbar.isActionEnabled -> { toolbar.disableAction() ; true }
            else -> super.onBackPressed()
        }
    }

    override fun onChangeHeight() {
        contentGlobalLayout(content,appBar)
        recyclerPosition()
    }

    override fun onStop() {
        super.onStop()
        interactor.stopRecyclerAdapter()
    }

    override fun onDetach() {
        interactor.onDetach()
        super.onDetach()
    }



}