package com.github.muneebwanee.dash.ui.fragments.photo

import android.annotation.SuppressLint
import android.os.Bundle
import com.github.clans.fab.FloatingActionButton
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.github.clans.fab.FloatingActionMenu
import com.github.muneebwanee.dash.R
import com.github.muneebwanee.dash.data.model.DataDelete
import com.github.muneebwanee.dash.data.preference.DataSharePreference.clearSelectedItem
import com.github.muneebwanee.dash.ui.fragments.base.BaseFragment
import com.github.muneebwanee.dash.ui.adapters.photoadapter.PhotoRecyclerAdapter
import com.github.muneebwanee.dash.ui.widget.OnScrollListener
import com.github.muneebwanee.dash.utils.ConstFun.customAnimationMenu
import com.github.muneebwanee.dash.utils.Consts.FRONT_FACING_CAMERA
import com.github.muneebwanee.dash.utils.Consts.REAR_FACING_CAMERA
import com.github.muneebwanee.dash.data.preference.DataSharePreference.statePersmissionPhotoShow
import com.github.muneebwanee.dash.data.preference.DataSharePreference.setSelectedItem
import com.github.muneebwanee.dash.ui.animation.OvershootInRightAnimator
import com.github.muneebwanee.dash.ui.widget.CustomRecyclerView
import com.github.muneebwanee.dash.ui.widget.toolbar.CustomToolbar
import com.github.muneebwanee.dash.utils.ConstFun.contentGlobalLayout
import com.github.muneebwanee.dash.utils.ConstFun.isScrollToolbar
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.database.DataSnapshot
import com.pawegio.kandroid.e
import com.pawegio.kandroid.hide
import com.pawegio.kandroid.runDelayedOnUiThread
import com.pawegio.kandroid.show
import kotterknife.bindView
import javax.inject.Inject

/**
 * Created by muneebwanee on 15/12/20.
 */
class PhotoFragment : BaseFragment(R.layout.fragment_photo), InterfaceViewPhoto {

    companion object { const val TAG = "PhotoFragment" }

    private var dataList : MutableList<DataDelete> = mutableListOf()

    private val viewNotHave: LinearLayout by bindView(R.id.not_have_placeholder)
    private val txtNotHave: TextView by bindView(R.id.txt_not_have_get)
    private val viewProgress: LinearLayout by bindView(R.id.progress_placeholder)
    private val viewFailed: LinearLayout by bindView(R.id.failed_placeholder)
    private val txtFailed: TextView by bindView(R.id.txt_failed_get)
    private val list: CustomRecyclerView by bindView(R.id.list)
    private val floatingButton: FloatingActionButton by bindView(R.id.floating_button_photo)
    private val content : ConstraintLayout by bindView(R.id.content)
    private val appBar : AppBarLayout by bindView(R.id.app_bar)
    private val toolbar : CustomToolbar by bindView(R.id.toolbar)
    private val main : CoordinatorLayout by bindView(R.id.main_view)
    private val menuGetPhoto: FloatingActionMenu by bindView(R.id.menu_get_photo)
    private val menuItemGetFrontPhoto: FloatingActionButton by bindView(R.id.menu_item_get_front_photo)
    private val menuItemGetRearPhoto: FloatingActionButton by bindView(R.id.menu_item_get_rear_photo)

    private var recyclerAdapter : PhotoRecyclerAdapter?=null

    @Inject lateinit var layoutM : LinearLayoutManager

    @Inject lateinit var interactor: InterfaceInteractorPhoto<InterfaceViewPhoto>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setToolbar(toolbar,true,R.string.search_photos,R.id.nav_clear_photo,R.string.search_photos_date)
        contentGlobalLayout(content,appBar)
        list.setAppBar(appBar)
        if (getComponent() != null) {
            getComponent()!!.inject(this)
            interactor.onAttach(this)
            startData()
            onClick()
            menuAction()
            timeConnection()
        }
    }

    private fun timeConnection() {
        runDelayedOnUiThread(13000) {
            if (viewProgress.isShown) failedResult(Throwable(getString(R.string.error_database_connection)))
        }
    }

    private fun startData(){
        interactor.setRecyclerAdapter()
        interactor.valueEventEnablePhoto()
        interactor.valueEventEnablePermission()
    }

    override fun onStart() {
        super.onStart()
        interactor.startRecyclerAdapter()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun menuAction() {
        customAnimationMenu(menuGetPhoto, R.drawable.ic_add_a_photo_black_24dp, R.drawable.ic_close_white_24dp)
        menuGetPhoto.setOnTouchListener(View.OnTouchListener { _, motionEvent ->
            if (menuGetPhoto.isOpened) {
                if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    closeMenu()
                    return@OnTouchListener true
                }
            }
            false
        })
    }

    private fun closeMenu() {
        menuGetPhoto.close(true)
        menuGetPhoto.isClickable = false
    }

    private fun onClick() {
        menuItemGetFrontPhoto.setOnClickListener {
            closeMenu()
            toolbar.showProgress = true
            interactor.getPhotoCamera(FRONT_FACING_CAMERA)
        }
        menuItemGetRearPhoto.setOnClickListener {
            closeMenu()
            toolbar.showProgress = true
            interactor.getPhotoCamera(REAR_FACING_CAMERA)
        }
    }

    override fun successResult(result: Boolean, filter:Boolean) {
        isScrollToolbar(toolbar,result)
        if (result) {
            viewProgress.hide()
            toolbar.showProgress = false
            viewNotHave.hide()
            viewFailed.hide()
            list.show()
            recyclerPosition()
        } else {
            toolbar.showProgress = false
            viewFailed.hide()
            floatingButton.hide(true)
            list.hide()
            appBar.setExpanded(true)
            if (filter) { viewProgress.show() ; viewNotHave.hide() }
            else{
                viewProgress.hide()
                viewNotHave.show()
                txtNotHave.text = getString(R.string.not_have_photo_yet)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun failedResult(throwable: Throwable) {
        viewProgress.hide()
        viewNotHave.hide()
        appBar.setExpanded(true)
        floatingButton.hide(true)
        list.hide()
        isScrollToolbar(toolbar,false)
        viewFailed.show()
        txtFailed.text = "${getString(R.string.failed_photo)}, ${throwable.message}"
    }

    override fun setRecyclerAdapter(recyclerAdapter: PhotoRecyclerAdapter) {
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
            addOnScrollListener(OnScrollListener(floatingButton, layoutM))
        }
        floatingButton.setOnClickListener {
            recyclerPosition()
        }
    }

    private fun recyclerPosition(){
        appBar.setExpanded(true)
        if (recyclerAdapter!=null) list.scrollToPosition(recyclerAdapter!!.itemCount-1)
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
            appBar.setExpanded(true)
            dataList = mutableListOf()
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
        } catch (t: Throwable) {
            e(TAG, t.message.toString())
        }
    }

    override fun setValuePermission(dataSnapshot: DataSnapshot) {
        try {
            if (dataSnapshot.exists()) {
                if (dataSnapshot.value as Boolean) {
                    activity!!.statePersmissionPhotoShow = true
                } else {
                    toolbar.showProgress = false
                    if (activity!!.statePersmissionPhotoShow)
                        showDialog(SweetAlertDialog.ERROR_TYPE, R.string.ops, getString(R.string.message_dialog_permission_photo_disable),
                                android.R.string.ok) {
                            setConfirmClickListener {
                                context.statePersmissionPhotoShow = false
                                hideDialog()
                            }
                            show()
                        }
                }
            }
        } catch (t: Throwable) {
            e(TAG, t.message.toString())
        }
    }

    override fun onSearchConfirmed(text: CharSequence) = interactor.setSearchQuery(text.toString())

    override fun onButtonClicked(buttonCode: Int) {
        when (buttonCode){
            CustomToolbar.BUTTON_BACK -> interactor.setSearchQuery("")
            CustomToolbar.BUTTON_ACTION_DELETE -> interactor.onDeleteData(dataList)
            CustomToolbar.BUTTON_STATE -> showSnackbar(if (toolbar.statePermission) R.string.enable_photo else R.string.disable_photo,main)
            CustomToolbar.BUTTON_CHILD_USER -> changeChild(TAG)
            else -> super.onButtonClicked(buttonCode)
        }
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
        interactor.stopRecyclerAdapter()
        interactor.onDetach()
        super.onDetach()
    }
}