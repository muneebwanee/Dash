package com.github.muneebwanee.dash.utils

import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter

/**
 * Created by muneebwanee on 15/12/20.
 */
class HomeWatcher(private val mContext: Context,private val listener: OnHomePressedListener) {

    interface OnHomePressedListener {
        fun onHomePressed()
        fun onRecentApps()
    }

    private val mFilter: IntentFilter = IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
    private var mReceiver: InnerReceiver? = null

    init {
        mReceiver = InnerReceiver()
    }

    fun startWatch() {
        if (mReceiver != null) mContext.registerReceiver(mReceiver, mFilter)
    }

    fun stopWatch() {
        if (mReceiver != null) mContext.unregisterReceiver(mReceiver)
    }

    internal inner class InnerReceiver : BroadcastReceiver() {
        private val SYSTEM_DIALOG_REASON_KEY = "reason"
        private val SYSTEM_DIALOG_REASON_HOME_KEY = "homekey"
        private val SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps"


        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == Intent.ACTION_CLOSE_SYSTEM_DIALOGS) {
                val reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY)
                if (reason != null) {
                    if (reason == SYSTEM_DIALOG_REASON_HOME_KEY) {
                        listener.onHomePressed()
                    }else if (reason == SYSTEM_DIALOG_REASON_RECENT_APPS){
                        listener.onRecentApps()
                    }
                }
            }
        }
    }
}