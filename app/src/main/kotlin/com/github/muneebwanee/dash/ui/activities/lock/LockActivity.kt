package com.github.muneebwanee.dash.ui.activities.lock

import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.view.KeyEvent
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.github.muneebwanee.dash.R
import com.github.muneebwanee.dash.ui.activities.base.BaseActivity
import com.github.muneebwanee.dash.data.preference.DataSharePreference.lockPin
import com.github.muneebwanee.dash.ui.widget.pinlockview.CustomPinLockView
import com.github.muneebwanee.dash.ui.widget.pinlockview.IndicatorDots
import com.github.muneebwanee.dash.ui.widget.pinlockview.PinLockListener
import com.github.muneebwanee.dash.utils.ConstFun.setVibrate
import com.github.muneebwanee.dash.utils.ConstFun.viewAnimation
import kotterknife.bindView

/**
 * Created by muneebwanee on 15/12/20.
 */
class LockActivity : BaseActivity(R.layout.activity_lock), PinLockListener {

    private val indicators: IndicatorDots by bindView(R.id.indicator_dots)
    private val lockView: CustomPinLockView by bindView(R.id.pin_lock_view)
    private val txtMsg: TextView by bindView(R.id.txt_msg_lock)

    private lateinit var vibrator: Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        startLock()
        initializeVibrator()
    }

    private fun initializeVibrator() {
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    private fun startLock() {
        lockView.attachIndicatorDots(indicators)
        lockView.setPinLockListener(this)
    }

    override fun onComplete(pin: String) {
        if (lockPin == pin) finish()
        else {
            setVibrate(150)
            txtMsg.viewAnimation(Techniques.Shake,200)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) return false
        return super.onKeyDown(keyCode, event)
    }

}
