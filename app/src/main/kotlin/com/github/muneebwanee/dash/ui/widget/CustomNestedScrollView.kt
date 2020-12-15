package com.github.muneebwanee.dash.ui.widget

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.widget.NestedScrollView
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.ceil

/**
 * Created by muneebwanee on 15/12/20.
 */
class CustomNestedScrollView : NestedScrollView {

    private var appBar : AppBarLayout? =null

    constructor(context: Context): super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        fitsSystemWindows = true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        requestApplyInsets()
    }

    override fun fitSystemWindows(insets: Rect): Boolean {
        setPadding(insets.left,topView(),insets.right,insets.bottom)
        return true
    }

    private fun topView() : Int = (if (appBar!=null) appBar!!.height else 0)

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        isScrollable()
        return super.onInterceptTouchEvent(e)
    }

    fun setAppBar(appBar: AppBarLayout){
        this.appBar = appBar
    }

    private fun isScrollable(){
        val childHeight = getChildAt(0).height
        val isScroll = height < childHeight + paddingTop + paddingBottom
        if (appBar != null) if (!isScroll) appBar!!.setExpanded(true)
    }

}