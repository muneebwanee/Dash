package com.github.muneebwanee.dash.ui.activities.login

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.*
import cn.pedant.SweetAlert.SweetAlertDialog
import com.github.muneebwanee.dash.R
import com.github.muneebwanee.dash.data.preference.DataSharePreference.childSelected
import com.github.muneebwanee.dash.data.preference.DataSharePreference.typeApp
import com.github.muneebwanee.dash.ui.activities.base.BaseActivity
import com.github.muneebwanee.dash.ui.activities.mainchild.MainChildActivity
import com.github.muneebwanee.dash.ui.activities.register.RegisterActivity
import com.github.muneebwanee.dash.ui.fragments.maps.MapsFragment
import com.github.muneebwanee.dash.utils.ConstFun.startAnimateActivity
import com.github.muneebwanee.dash.utils.ConstFun.isShow
import com.github.muneebwanee.dash.utils.ConstFun.startMain
import com.github.muneebwanee.dash.utils.Consts.TEXT
import com.github.muneebwanee.dash.utils.Consts.TYPE_CHILD
import com.github.muneebwanee.dash.utils.Consts.TYPE_PARENT
import com.github.muneebwanee.dash.utils.KeyboardUtils
import com.jaredrummler.materialspinner.MaterialSpinner
import kotterknife.bindView
import javax.inject.Inject


/**
 * Created by muneebwanee on 15/12/20.
 */
class LoginActivity : BaseActivity(R.layout.activity_login), InterfaceViewLogin, KeyboardUtils.SoftKeyboardToggleListener {

    private val edtEmail: EditText by bindView(R.id.edit_login_email)
    private val edtPass: EditText by bindView(R.id.edit_login_password)
    private val btnSignIn: Button by bindView(R.id.btn_login_signin)
    private val btnSignUp: TextView by bindView(R.id.txt_login_signup)
    private val spinnerType : MaterialSpinner by bindView(R.id.spinner_type)
    private val edtNewChild : EditText by bindView(R.id.edit_new_child)
    private val scroll : ScrollView by bindView(R.id.scroll)

    @Inject
    lateinit var interactor: InterfaceInteractorLogin<InterfaceViewLogin>

    override fun onStart() {
        super.onStart()
        if (interactor.user() != null){
            if (typeApp) startMain(MapsFragment.TAG)
            else startAnimateActivity<MainChildActivity>(R.anim.fade_in,R.anim.fade_out)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getComponent()!!.inject(this)
        interactor.onAttach(this)
        initializeSpinner()
        newChildValidationObservable(edtNewChild)
        emailValidationObservable(edtEmail)
        passValidationObservable(edtPass)
        signInValidationObservable(btnSignIn)
        onClickLogin()
        KeyboardUtils.addKeyboardToggleListener(this,this)
    }

    override fun onToggleSoftKeyboard(isVisible: Boolean) {
        if (isVisible) scroll.smoothScrollTo(0,scroll.bottom)
    }

    override fun onDestroy() {
        interactor.onDetach()
        super.onDestroy()
    }

    private fun onClickLogin() {
        btnSignUp.setOnClickListener {
            startAnimateActivity<RegisterActivity>(R.anim.slide_in_right,R.anim.slide_out_left)
        }
        btnSignIn.setOnClickListener {
            checkData { checkPermissionType { signIn() } }
        }
    }

    private fun checkData(func:() -> Unit){
        if (!typeApp){
            if (!TEXT.matcher(edtNewChild.text).matches()){
                edtNewChild.text.clear()
                edtNewChild.error = getString(R.string.characters_child)
                edtNewChild.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_child_care_red,0,0,0)
                edtNewChild.requestFocus()
            }else func()
        }else func()
    }

    @SuppressLint("CheckResult")
    private fun checkPermissionType(func:() -> Unit ){
        if (!typeApp){
            getPermissions()!!.requestEachCombined(Manifest.permission.CALL_PHONE,Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS, Manifest.permission.CAMERA,Manifest.permission.PROCESS_OUTGOING_CALLS)
                    .subscribe { permission -> subscribePermission(permission,getString(R.string.message_permission),getString(R.string.message_permission_never_ask_again)){
                        func()
                    }}
        }else func()
    }

    private fun signIn(){
        interactor.signInDisposable(edtEmail.text.toString(),edtPass.text.toString())
    }

    private fun initializeSpinner(){
        edtNewChild.isShow(!typeApp)
        btnSignUp.isShow(!typeApp)
        spinnerType.setItems(TYPE_CHILD, TYPE_PARENT)
        spinnerType.selectedIndex = if (typeApp) 1 else 0
        spinnerType.setOnItemSelectedListener { _, _, _, item ->
            typeApp = item != TYPE_CHILD
            edtNewChild.isShow(item == TYPE_CHILD)
            btnSignUp.isShow(item == TYPE_CHILD)
        }
    }

    override fun successResult(result: Boolean, filter:Boolean) {
        hideDialog()
        if (result) {
            showMessage(getString(R.string.login_success))
            if (typeApp) startMain(MapsFragment.TAG)
            else {
                childSelected = edtNewChild.text.toString()
                startAnimateActivity<MainChildActivity>(R.anim.fade_in,R.anim.fade_out)
            }
        } else {
            showError(getString(R.string.login_failed_try_again_later))
        }
    }

    override fun failedResult(throwable: Throwable) {
        hideDialog()
        showDialog(SweetAlertDialog.ERROR_TYPE, R.string.ops, "${getString(R.string.login_failed)} ${throwable.message}", android.R.string.ok) {
            setCanceledOnTouchOutside(true)
            show()
        }
    }


}