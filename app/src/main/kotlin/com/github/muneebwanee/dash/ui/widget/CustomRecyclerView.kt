package com.github.muneebwanee.dash.ui.widget

import android.content.Context
import android.graphics.Rect
import android.os.Build
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.animation.AnimationUtils
import android.view.animation.GridLayoutAnimationController
import com.github.muneebwanee.dash.R
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.ceil

/**
 * Created by muneebwanee on 15/12/20.
 */
class CustomRecyclerView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle){

    private var appBar : AppBarLayout? =null

    init {
        fitsSystemWindows = true
    }

    fun setAppBar(appBar: AppBarLayout){
        this.appBar = appBar
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

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (!isInEditMode) {
            layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.grid_layout_animation)
            startLayoutAnimation()
        }
    }

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        turnOffNestedScrollingIfEnoughItems()
        return super.onInterceptTouchEvent(e)
    }

    private fun turnOffNestedScrollingIfEnoughItems(){
        val manager = layoutManager as LinearLayoutManager
        val count = if (manager.itemCount <= 0) 0 else manager.itemCount - 1
        val isFirstVisible = manager.findFirstCompletelyVisibleItemPosition() == 0
        val isLastItemVisible = manager.findLastCompletelyVisibleItemPosition() == count
        isNestedScrollingEnabled = !(isLastItemVisible && isFirstVisible)
        if (appBar != null) if (isFirstVisible && isLastItemVisible) appBar!!.setExpanded(true)
    }

    override fun attachLayoutAnimationParameters(child: View, params: ViewGroup.LayoutParams, index: Int, count: Int) {
        if (adapter != null && layoutManager is LinearLayoutManager) {

            var animParams: GridLayoutAnimationController.AnimationParameters? = params.layoutAnimationParameters as GridLayoutAnimationController.AnimationParameters?

            if (animParams == null) {
                animParams = GridLayoutAnimationController.AnimationParameters()
                params.layoutAnimationParameters = animParams
            }

            val columns = (layoutManager as LinearLayoutManager).childCount
            animParams.count = count
            animParams.index = index
            animParams.columnsCount = columns
            animParams.rowsCount = count / columns

            val invertedIndex = count - 1 - index
            animParams.column = columns - 1 - invertedIndex % columns
            animParams.row = animParams.rowsCount - 1 - invertedIndex / columns
        } else {
            super.attachLayoutAnimationParameters(child, params, index, count)
        }
    }


    private val dataObserver: AdapterDataObserver = object : AdapterDataObserver() {

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            setScrollPosition(positionStart)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            setScrollPosition(positionStart)
        }
    }

    private fun setScrollPosition(positionStart: Int) {
        val lastVisiblePosition = (layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
        if (lastVisiblePosition == -1 || positionStart >= adapter!!.itemCount - 1 && lastVisiblePosition == positionStart - 1) scrollToPosition(positionStart)
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        if (adapter != null) {
            adapter.registerAdapterDataObserver(dataObserver)
            dataObserver.onChanged()
        }
    }
}