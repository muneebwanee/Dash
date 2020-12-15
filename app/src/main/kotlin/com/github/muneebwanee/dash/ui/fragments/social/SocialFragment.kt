package com.github.muneebwanee.dash.ui.fragments.social

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.github.muneebwanee.dash.R
import com.github.muneebwanee.dash.data.model.Social
import com.github.muneebwanee.dash.ui.fragments.base.BaseFragment
import com.github.muneebwanee.dash.ui.widget.toolbar.CustomToolbar
import com.github.muneebwanee.dash.utils.ConstFun.isScrollToolbar
import com.github.muneebwanee.dash.utils.Consts
import com.google.firebase.database.DataSnapshot
import com.pawegio.kandroid.e
import com.pawegio.kandroid.hide
import com.pawegio.kandroid.show
import kotterknife.bindView
import javax.inject.Inject

/**
 * Created by muneebwanee on 15/12/20.
 */
class SocialFragment : BaseFragment(R.layout.fragment_social), InterfaceViewSocial {

    companion object { const val TAG = "SocialFragment" }

    private val viewDone: ImageView by bindView(R.id.done_social)
    private val viewProgress: LinearLayout by bindView(R.id.progress_placeholder)
    private val viewCredentials: LinearLayout by bindView(R.id.view_credentials)
    private val viewFailed: LinearLayout by bindView(R.id.failed_placeholder)
    private val txtFailed: TextView by bindView(R.id.txt_failed_get)
    private val txtEmail: TextView by bindView(R.id.txt_email_get_social)
    private val txtPass: TextView by bindView(R.id.txt_pass_get_social)
    private val toolbar : CustomToolbar by bindView(R.id.toolbar)
    private val main : CoordinatorLayout by bindView(R.id.main_view)

    @Inject lateinit var interactor: InterfaceInteractorSocial<InterfaceViewSocial>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setToolbar(toolbar,false,R.string.social,R.id.nav_clear_social)
        isScrollToolbar(toolbar,false)
        if (getComponent() != null) {
            getComponent()!!.inject(this)
            interactor.onAttach(this)
        }
    }

    override fun onStart() {
        super.onStart()
        interactor.valueEventSocial()
        interactor.valueEventEnablePermission()
    }

    override fun successResult(dataSnapshot: DataSnapshot) {
        if (dataSnapshot.exists()) {
            val social = dataSnapshot.getValue(Social::class.java)!!
            viewCredentials.show()
            viewProgress.hide()
            viewDone.show()
            txtEmail.text = social.emailSocial
            txtPass.text = social.passSocial
        } else {
            viewCredentials.show()
            viewDone.hide()
            viewProgress.show()
            txtEmail.text = getString(R.string.ellipsis)
            txtPass.text = getString(R.string.ellipsis)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun failedResult(throwable: Throwable) {
        viewCredentials.hide()
        viewFailed.show()
        txtFailed.text = "${getString(R.string.failed_social)}, ${throwable.message.toString()}"
    }

    override fun setValuePermission(dataSnapshot: DataSnapshot) {
        toolbar.enableStatePermission = true
        try {
            if (dataSnapshot.exists()) {
                if (dataSnapshot.value as Boolean) toolbar.statePermission = true
                else {
                    toolbar.statePermission = false
                    failedResult(Throwable(getString(R.string.message_permission_social_disable)))
                }
            } else toolbar.statePermission = false
        } catch (t: Throwable) {
            e(Consts.TAG, t.message.toString())
        }
    }

    override fun onButtonClicked(buttonCode: Int) {
        when(buttonCode){
            CustomToolbar.BUTTON_STATE -> showSnackbar(if (toolbar.statePermission) R.string.enable_social else R.string.disable_social,main)
            CustomToolbar.BUTTON_CHILD_USER -> changeChild(TAG)
            else -> super.onButtonClicked(buttonCode)
        }
    }

    override fun onDetach() {
        interactor.onDetach()
        super.onDetach()
    }

}