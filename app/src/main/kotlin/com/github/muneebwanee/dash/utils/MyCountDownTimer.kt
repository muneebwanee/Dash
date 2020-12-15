package com.github.muneebwanee.dash.utils

import android.os.CountDownTimer
import com.github.muneebwanee.dash.utils.Consts.TAG
import com.pawegio.kandroid.i

/**
 * Created by muneebwanee on 15/12/20.
 */
class MyCountDownTimer(startTime: Long, interval: Long,private val timer:((timer:Long)->Unit)?=null,
                       private val func: () -> Unit) : CountDownTimer(startTime, interval) {
    override fun onFinish() = func()
    override fun onTick(t: Long) { i(TAG,"timer $t") ; timer?.invoke(t) }
}