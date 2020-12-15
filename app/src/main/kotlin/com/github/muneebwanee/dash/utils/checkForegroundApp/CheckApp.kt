package com.github.muneebwanee.dash.utils.checkForegroundApp

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import com.pawegio.kandroid.runOnUiThread

/**
 * Created by muneebwanee on 15/12/20.
 */
class CheckApp(private val context: Context, private val action: (app: String?) -> Unit) {

    private val detector: CheckDetector = CheckForegroundApp()

    private var timeout:Long = 1000

    private val thread: Thread = Thread(Runnable {
        while (true) {
            try {
                Thread.sleep(timeout)
                startHandler()
            } catch (e: InterruptedException) {
                break
            }
        }
    })

    fun setTimeout(time: Long): CheckApp {
        this.timeout = time
        return this
    }

    fun start(): CheckApp {
        thread.start()
        return this
    }

    fun stop() {
        thread.interrupt()
    }

    private fun startHandler() {
        runOnUiThread {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
                action(detector.getForegroundPostLollipop(context))
            else
                action(detector.getForegroundPreLollipop(context))
        }
    }

}