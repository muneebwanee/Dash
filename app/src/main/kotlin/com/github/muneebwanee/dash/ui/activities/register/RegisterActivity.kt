package com.github.muneebwanee.dash.ui.activities.register

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.github.muneebwanee.dash.R
import com.github.muneebwanee.dash.data.preference.DataSharePreference.childSelected
import com.github.muneebwanee.dash.ui.activities.base.BaseActivity
import com.github.muneebwanee.dash.ui.activities.login.LoginActivity
import com.github.muneebwanee.dash.ui.activities.mainchild.MainChildActivity
import com.github.muneebwanee.dash.utils.ConstFun.startAnimateActivity
import com.github.muneebwanee.dash.utils.Consts.TEXT
import com.github.muneebwanee.dash.utils.KeyboardUtils
import kotterknife.bindView
import javax.inject.Inject

/**
 * Created by muneebwanee on 15/12/20.
 */
class RegisterActivity : BaseActivity(R.layout.activity_register), InterfaceViewRegister, KeyboardUtils.SoftKeyboardToggleListener {

    private val edtNewChild: EditText by bindView(R.id.edit_new_child_register)
    private val edtEmail: EditText by bindView(R.id.edit_register_email)
    private val edtPass: EditText by bindView(R.id.edit_register_password)
    private val edtPassRepeat: EditText by bindView(R.id.edit_register_repeat_password)
    private val btnSignUp: Button by bindView(R.id.btn_register_sign_up)
    private val btnHaveAccount : Button by bindView(R.id.btn_register_have_account)
    private val scroll : ScrollView by bindView(R.id.scroll)

    @Inject
    lateinit var interactor: InterfaceInteractorRegister<InterfaceViewRegister>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getComponent()!!.inject(this)
        interactor.onAttach(this)
        newChildValidationObservable(edtNewChild)
        emailValidationObservable(edtEmail)
        passValidationObservable(edtPass)
        passValidationObservable(edtPassRepeat)
        signInValidationObservable(btnSignUp)
        onClickRegister()
        KeyboardUtils.addKeyboardToggleListener(this,this)
    }

    override fun onToggleSoftKeyboard(isVisible: Boolean) {
        if (isVisible) scroll.smoothScrollTo(0,scroll.bottom)
    }

    override fun onDestroy() {
        interactor.onDetach()
        super.onDestroy()
    }

    private fun onClickRegister() {
        btnHaveAccount.setOnClickListener { startAnimateActivity<LoginActivity>(R.anim.slide_in_left,R.anim.slide_out_right) }
        btnSignUp.setOnClickListener {
            if (!TEXT.matcher(edtNewChild.text).matches()){
                edtNewChild.text.clear()
                edtNewChild.error = getString(R.string.characters_child)
                edtNewChild.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_child_care_red,0,0,0)
                edtNewChild.requestFocus()
            }else if (edtPass.text.toString() != edtPassRepeat.text.toString()) {
                edtPassRepeat.text.clear()
                edtPass.text.clear()
                showError(getString(R.string.register_pass_match))
            }else login()
        }
    }

    @SuppressLint("CheckResult")
    private fun login(){
        getPermissions()!!.requestEachCombined(Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS, Manifest.permission.CAMERA,Manifest.permission.PROCESS_OUTGOING_CALLS)
                    .subscribe {permission -> subscribePermission(permission,getString(R.string.message_permission),getString(R.string.message_permission_never_ask_again)){
                        interactor.signUpDisposable(edtEmail.text.toString(), edtPass.text.toString())
                    }}
    }

    override fun successResult(result: Boolean, filter:Boolean) {
        hideDialog()
        if (result) {
            childSelected = edtNewChild.text.toString()
            showMessage(getString(R.string.login_success))
            startAnimateActivity<MainChildActivity>(R.anim.fade_in,R.anim.fade_out)
        } else {
            showError(getString(R.string.sign_up_failed_try_again_later))
        }
    }

    override fun failedResult(throwable: Throwable) {
        hideDialog()
        showDialog(SweetAlertDialog.ERROR_TYPE, R.string.ops, "${getString(R.string.sign_up_failed)} ${throwable.message}", android.R.string.ok) {
            setCanceledOnTouchOutside(true)
            show()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) startAnimateActivity<LoginActivity>(R.anim.slide_in_left,R.anim.slide_out_right)
        return super.onKeyDown(keyCode, event)
    }

}