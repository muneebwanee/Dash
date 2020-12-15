package com.github.muneebwanee.dash.ui.widget.pinlockview

import android.animation.LayoutTransition
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import com.github.muneebwanee.dash.R


/**
 * Created by muneebwanee on 15/12/20.
 */
class IndicatorDots(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : LinearLayout(context, attrs, defStyleAttr) {

    private var mDotDiameter: Int = 0
    private var mDotSpacing: Int = 0
    private var mFillDrawable: Int = 0
    private var mPreviousLength: Int = 0

    constructor(context: Context) : this(context, null) { init() }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) { init() }

    init { init() }

    private fun init() {

        mDotDiameter = resources.getDimension(R.dimen.default_dot_diameter).toInt()
        mDotSpacing = resources.getDimension(R.dimen.default_dot_spacing).toInt()
        mFillDrawable = R.drawable.dot_filled

        ViewCompat.setLayoutDirection(this, ViewCompat.LAYOUT_DIRECTION_LTR)
        layoutTransition = LayoutTransition()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val params = this.layoutParams
        params.height = mDotDiameter
        requestLayout()
    }

    fun updateDot(length: Int) {
        if (length > 0) {
            if (length > mPreviousLength) {
                val dot = View(context)
                fillDot(dot)

                val params = LayoutParams(
                    mDotDiameter,
                    mDotDiameter
                )
                params.setMargins(mDotSpacing, 0, mDotSpacing, 0)
                dot.layoutParams = params

                addView(dot, length - 1)
            } else {
                removeViewAt(length)
            }
            mPreviousLength = length
        } else {
            removeAllViews()
            mPreviousLength = 0
        }
    }

    private fun fillDot(dot: View) {
        dot.setBackgroundResource(mFillDrawable)
    }

}
