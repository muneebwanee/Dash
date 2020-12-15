package com.github.muneebwanee.dash.ui.fragments.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.PopupMenu
import android.view.*
import androidx.annotation.LayoutRes
import cn.pedant.SweetAlert.SweetAlertDialog
import com.github.muneebwanee.dash.di.component.ActivityComponent
import com.github.muneebwanee.dash.ui.widget.toolbar.CustomToolbar
import com.github.muneebwanee.dash.data.preference.DataSharePreference.childPhoto
import com.github.muneebwanee.dash.ui.activities.base.BaseActivity
import com.github.muneebwanee.dash.ui.activities.base.InterfaceView
import com.tbruyelle.rxpermissions2.Permission
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.Disposable

/**
 * Created by muneebwanee on 15/12/20.
 */
abstract class BaseFragment(@LayoutRes layout:Int) : Fragment(layout), InterfaceView, CustomToolbar.OnToolbarActionListener, IOnFragmentListener {

    companion object {
        @JvmStatic
        var baseActivity: BaseActivity? = null
    }

    private var toolbar : CustomToolbar?=null
    private var titleInt : Int = 0
    private var hintInt : Int = 0

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        baseActivity = (activity as BaseActivity)
    }

    override fun getComponent(): ActivityComponent? =
            if (baseActivity != null) baseActivity!!.getComponent() else null

    override fun getPermissions(): RxPermissions? = if (baseActivity != null) baseActivity!!.getPermissions() else null

    override fun subscribePermission(permission: Permission, msgRationale: String, msgDenied: String, granted: () -> Unit) {
        if (baseActivity != null) baseActivity!!.subscribePermission(permission, msgRationale, msgDenied, granted)
    }

    override fun showMessage(message: String) {
        if (baseActivity != null)
            baseActivity!!.showMessage(message)
    }

    override fun showMessage(message: Int) {
        if (baseActivity !=null)
            baseActivity!!.showMessage(message)
    }

    override fun showSnackbar(message: Int,v:View) {
        if (baseActivity !=null)
            baseActivity!!.showSnackbar(message,v)
    }

    override fun showSnackbar(message: String,v:View) {
        if (baseActivity != null)
            baseActivity!!.showSnackbar(message,v)
    }

    override fun showDialog(alertType: Int, title: Int, msg: String?, txtPositiveButton: Int?, cancellable: Boolean, action: SweetAlertDialog.() -> Unit) : SweetAlertDialog =
            baseActivity!!.showDialog(alertType, title, msg, txtPositiveButton, cancellable, action)


    override fun hideDialog() {
        if (baseActivity != null)
            baseActivity!!.hideDialog()
    }


    override fun showError(message: String) {
        if (baseActivity != null)
            baseActivity!!.showError(message)
    }


    override fun addDisposable(disposable: Disposable) {
        if (baseActivity != null)
            baseActivity!!.addDisposable(disposable)
    }

    override fun clearDisposable() {
        if (baseActivity != null)
            baseActivity!!.clearDisposable()
    }

    override fun changeChild(fragmentTag: String){
        if (baseActivity !=null)
            baseActivity!!.changeChild(fragmentTag)
    }

    override fun setToolbar(toolbar: CustomToolbar,showSearch:Boolean,title:Int,showItemMenu:Int,hint:Int) {
        this.toolbar = toolbar
        this.titleInt = title
        this.hintInt = hint
        toolbar.setOnToolbarActionListener(this)
        toolbar.setTitle = getString(title)
        if (hint!=0) toolbar.hint = getString(hint)
        toolbar.enableSearch = showSearch
        if (context!!.childPhoto!="") toolbar.setChildPhoto(context!!.childPhoto)
        val menu = toolbar.menu
        if (showItemMenu!=0) menu!!.menu.findItem(showItemMenu).isVisible = true
        if (baseActivity !=null) baseActivity!!.setMenu(menu)
    }

    override fun onSearchStateChanged(enabled: Boolean) {
        if (baseActivity !=null){
            if (enabled) baseActivity!!.setDrawerLock()
            else baseActivity!!.setDrawerUnLock()
        }
    }

    override fun onSearchConfirmed(text: CharSequence) {}

    override fun onButtonClicked(buttonCode: Int) {
        if (baseActivity !=null){
            when(buttonCode){
                CustomToolbar.BUTTON_NAVIGATION -> baseActivity!!.openDrawer()
            }
        }
    }

    override fun onActionStateChanged(enabled: Boolean) {
        if (baseActivity !=null){
            if (enabled) baseActivity!!.setDrawerLock()
            else {
                toolbar?.setTitle = getString(titleInt)
                if (hintInt != 0) toolbar?.hint = getString(hintInt)
                baseActivity!!.setDrawerUnLock()
            }
        }
    }

    override fun setActionToolbar(action: Boolean) {
        if (action) toolbar!!.enableAction()
        else toolbar!!.disableAction()
    }

    override fun onChangeHeight() {}
    override fun onHideFragment() {}
    override fun onBackPressed(): Boolean = false
    override fun successResult(result: Boolean, filter:Boolean) {}
    override fun failedResult(throwable: Throwable) {}
    override fun onItemClick(key: String?, child: String,file: String,position:Int) {}
    override fun onItemLongClick(key: String?, child: String,file: String,position:Int) {}

    interface Callback{
        fun setDrawerLock()
        fun setDrawerUnLock()
        fun openDrawer()
        fun setMenu(menu: PopupMenu?)
    }

}