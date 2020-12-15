package com.github.muneebwanee.dash.ui.activities.base

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import cn.pedant.SweetAlert.SweetAlertDialog
import com.github.muneebwanee.dash.R
import com.github.muneebwanee.dash.di.component.ActivityComponent
import com.github.muneebwanee.dash.di.component.DaggerActivityComponent
import com.github.muneebwanee.dash.di.module.ActivityModule
import com.github.muneebwanee.dash.utils.ConstFun.alertDialog
import com.github.muneebwanee.dash.utils.ConstFun.openAppSystemSettings
import com.jakewharton.rxbinding2.widget.RxTextView
import com.pawegio.kandroid.longToast
import com.tbruyelle.rxpermissions2.Permission
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.widget.PopupMenu
import android.view.View
import android.view.WindowManager
import androidx.annotation.LayoutRes
import com.github.muneebwanee.dash.app.Dash
import com.github.muneebwanee.dash.ui.fragments.base.BaseFragment
import com.github.muneebwanee.dash.ui.widget.toolbar.CustomToolbar
import com.github.muneebwanee.dash.utils.ConstFun.adjustFontScale
import com.github.muneebwanee.dash.utils.ConstFun.isAndroidM
import com.github.muneebwanee.dash.utils.Consts.TEXT


/**
 * Created by muneebwanee on 15/12/20.
 */
abstract class BaseActivity(@LayoutRes layout:Int) : AppCompatActivity(layout), InterfaceView, BaseFragment.Callback {

    private var alertDialog: SweetAlertDialog? = null
    private var compositeDisposable: CompositeDisposable? = null
    private var rxPermissions: RxPermissions? = null
    private lateinit var snackbar: Snackbar

    companion object {
        @JvmStatic
        var activityComponent: ActivityComponent? = null
    }

    private lateinit var emailObservable: Observable<Boolean>
    private lateinit var passObservable: Observable<Boolean>
    private lateinit var signInEnabled: Observable<Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeActivityComponent()
        adjustFontScale()
    }

    fun windowLightStatusBar(){
        if (isAndroidM()) window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        else window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    }

    override fun onResume() {
        super.onResume()
        initializeActivityComponent()
    }

    private fun initializeActivityComponent() {
        activityComponent = DaggerActivityComponent.builder()
                .activityModule(ActivityModule(this))
                .appComponent(Dash.appComponent).build()
        compositeDisposable = CompositeDisposable()
        rxPermissions = RxPermissions(this)
    }

    override fun getComponent(): ActivityComponent? = activityComponent

    override fun getPermissions(): RxPermissions? = rxPermissions

    override fun subscribePermission(permission: Permission, msgRationale: String, msgDenied: String, granted: () -> Unit) {
        when {
            permission.granted -> granted()
            permission.shouldShowRequestPermissionRationale -> showDialog(SweetAlertDialog.WARNING_TYPE, R.string.title_dialog, msgRationale, android.R.string.ok) {
                setCanceledOnTouchOutside(true)
                show()
            }
            else -> showDialog(SweetAlertDialog.WARNING_TYPE, R.string.title_dialog, msgDenied, R.string.go_to_setting, true) {
                setConfirmClickListener { openAppSystemSettings() }
                show()
            }
        }
    }


    override fun showDialog(alertType: Int, title: Int, msg: String?, txtPositiveButton: Int?, cancellable: Boolean, action: SweetAlertDialog.() -> Unit) : SweetAlertDialog {
        alertDialog = alertDialog(alertType, title, msg, txtPositiveButton, cancellable, action)
        return alertDialog!!
    }

    override fun hideDialog() {
        if (alertDialog != null) alertDialog!!.dismissWithAnimation()
    }

    override fun showError(message: String) {
        showMessage(message)
    }

    override fun showMessage(message: Int) {
        showMessage(getString(message))
    }

    override fun showMessage(message: String) {
        longToast(message)
    }

    override fun showSnackbar(message: Int, v: View) {
        snackbar = Snackbar.make(v, message, Snackbar.LENGTH_LONG)
                .setAction(android.R.string.ok){snackbar.dismiss()}
        snackbar.show()
    }

    override fun showSnackbar(message: String, v: View) {
        snackbar = Snackbar.make(v, message, Snackbar.LENGTH_INDEFINITE)
                .setAction(android.R.string.ok){snackbar.dismiss()}
        snackbar.show()
    }

    override fun addDisposable(disposable: Disposable) {
        compositeDisposable!!.add(disposable)
    }

    override fun clearDisposable() {
        if (compositeDisposable != null) {
            compositeDisposable!!.dispose()
            compositeDisposable!!.clear()
        }
    }

    @SuppressLint("CheckResult")
    fun newChildValidationObservable(newChild : EditText){
        RxTextView.textChanges(newChild).map { textNewChild -> TEXT.matcher(textNewChild).matches()  }
                .distinctUntilChanged().map { b -> if (b) R.drawable.ic_child_care else R.drawable.ic_child_care_black_24dp }
                .subscribe { id -> newChild.setCompoundDrawablesWithIntrinsicBounds(id,0,0,0) }
    }

    /** Email validation */
    fun emailValidationObservable(edtEmail: EditText) {
        emailObservable = RxTextView.textChanges(edtEmail).map { textEmail -> Patterns.EMAIL_ADDRESS.matcher(textEmail).matches() }
        emailObservable(edtEmail)
    }

    @SuppressLint("CheckResult")
    private fun emailObservable(edtEmail: EditText) {
        emailObservable.distinctUntilChanged().map { b -> if (b) R.drawable.ic_user else R.drawable.ic_user_alert }
                .subscribe { id -> edtEmail.setCompoundDrawablesWithIntrinsicBounds(id, 0, 0, 0) }
    }

    /** Password validation */
    fun passValidationObservable(edtPass: EditText) {
        passObservable = RxTextView.textChanges(edtPass).map { textPass -> textPass.length > 5 }
        passObservable(edtPass)
    }

    @SuppressLint("CheckResult")
    private fun passObservable(edtPass: EditText) {
        passObservable.distinctUntilChanged().map { b -> if (b) R.drawable.ic_lock_open else R.drawable.ic_lock }
                .subscribe { id -> edtPass.setCompoundDrawablesWithIntrinsicBounds(id, 0, 0, 0) }
    }

    /** Sign In observer */
    fun signInValidationObservable(btnSignIn: Button) {
        signInEnabled = Observable.combineLatest(emailObservable, passObservable, BiFunction { email, pass -> email && pass })
        signInEnableObservable(btnSignIn)
    }

    @SuppressLint("CheckResult")
    private fun signInEnableObservable(btnSignIn: Button) {
        signInEnabled.distinctUntilChanged().subscribe { enabled -> btnSignIn.isEnabled = enabled }
        signInEnabled.distinctUntilChanged()
                .map { b -> if (b) R.color.colorGreen else R.color.colorTextDisabled }
                .subscribe { color -> btnSignIn.backgroundTintList = ContextCompat.getColorStateList(this, color) }

    }

    override fun setActionToolbar(action:Boolean) {}
    override fun successResult(result: Boolean, filter:Boolean) {}
    override fun failedResult(throwable: Throwable) {}
    override fun onItemClick(key: String?, child: String,file: String,position:Int) {}
    override fun onItemLongClick(key: String?, child: String,file: String,position:Int) {}
    override fun setDrawerLock() {}
    override fun setDrawerUnLock() {}
    override fun openDrawer() {}
    override fun setMenu(menu: PopupMenu?) {}
    override fun changeChild(fragmentTag:String) {}
    override fun setToolbar(toolbar: CustomToolbar, showSearch: Boolean, title: Int, showItemMenu: Int,hint:Int) {}

}

