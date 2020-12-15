package com.github.muneebwanee.dash.utils

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import java.util.*

/**
 * Created by muneebwanee on 15/12/20.
 */
class KeyboardUtils private constructor(activity: Activity, private var callback: SoftKeyboardToggleListener?) : ViewTreeObserver.OnGlobalLayoutListener {

    private val rootView: View = (activity.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0)
    private var prevValue: Boolean? = null
    private var screenDensity = 1f

    interface SoftKeyboardToggleListener {
        fun onToggleSoftKeyboard(isVisible: Boolean)
    }

    init {
        rootView.viewTreeObserver.addOnGlobalLayoutListener(this)
        screenDensity = activity.resources.displayMetrics.density
    }


    override fun onGlobalLayout() {
        val r = Rect()
        rootView.getWindowVisibleDisplayFrame(r)

        val heightDiff = rootView.rootView.height - (r.bottom - r.top)
        val dp = heightDiff / screenDensity
        val isVisible = dp > MAGIC_NUMBER

        if (callback != null && (prevValue == null || isVisible != prevValue)) {
            prevValue = isVisible
            callback!!.onToggleSoftKeyboard(isVisible)
        }
    }

    private fun removeListener() {
        callback = null
        rootView.viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

    companion object {
        private const val MAGIC_NUMBER = 200
        private val sListenerMap = HashMap<SoftKeyboardToggleListener, KeyboardUtils>()

        fun addKeyboardToggleListener(activity: Activity, listener: SoftKeyboardToggleListener) {
            removeKeyboardToggleListener(listener)
            sListenerMap[listener] = KeyboardUtils(activity, listener)
        }

        private fun removeKeyboardToggleListener(listener: SoftKeyboardToggleListener) {
            if (sListenerMap.containsKey(listener)) {
                val k = sListenerMap[listener]
                k!!.removeListener()

                sListenerMap.remove(listener)
            }
        }

        /*fun hiddenKeyboard(activity: Activity) {
            val view = activity.currentFocus
            if (view != null) {
                val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }*/
    }

}