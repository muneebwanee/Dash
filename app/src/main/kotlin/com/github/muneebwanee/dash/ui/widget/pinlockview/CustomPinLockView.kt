package com.github.muneebwanee.dash.ui.widget.pinlockview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.muneebwanee.dash.R

/**
 * Created by muneebwanee on 15/12/20.
 */
class CustomPinLockView : RecyclerView,
    PinLockAdapter.OnPinButtonClickListener {

    private var mPin = ""

    private var mHorizontalSpacing: Int = 0
    private var mVerticalSpacing: Int = 0

    private var mTextSize: Int = 0
    private var mButtonSize: Int = 0
    private var mDeleteButtonSize: Int = 0

    private var mIndicatorDots: IndicatorDots? = null
    private var mAdapter: PinLockAdapter? = null
    private var mPinLockListener: PinLockListener? = null
    private var mCustomizationOptionsBundle: CustomizationOptionsBundle? = null

    private val isIndicatorDotsAttached: Boolean
        get() = mIndicatorDots != null

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs)
    }

    private fun init(attributeSet: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CustomPinLockView)
        try {
            mHorizontalSpacing = resources.getDimension(R.dimen.default_horizontal_spacing).toInt()
            mVerticalSpacing = resources.getDimension(R.dimen.default_vertical_spacing).toInt()
            mTextSize = typedArray.getDimension(R.styleable.CustomPinLockView_TextSize, resources.getDimension(R.dimen.default_text_size)).toInt()
            mButtonSize = typedArray.getDimension(R.styleable.CustomPinLockView_ButtonSize, resources.getDimension(R.dimen.default_button_size)).toInt()
            mDeleteButtonSize = typedArray.getDimension(R.styleable.CustomPinLockView_DeleteButtonSize, resources.getDimension(R.dimen.default_delete_button_size)).toInt()
        } finally {
            typedArray.recycle()
        }
        mCustomizationOptionsBundle = CustomizationOptionsBundle(
            mTextSize,
            mButtonSize,
            mDeleteButtonSize
        )
        initView()
    }

    private fun initView() {
        layoutManager = CustomGridLayoutManager(context, 3)

        mAdapter = PinLockAdapter()
        mAdapter!!.setOnPinButtonClickListener(this)
        mAdapter!!.setCustomizationOptions(mCustomizationOptionsBundle!!)
        adapter = mAdapter

        addItemDecoration(
            ItemSpaceDecoration(
                mHorizontalSpacing,
                mVerticalSpacing,
                3,
                false
            )
        )
        overScrollMode = View.OVER_SCROLL_NEVER
    }

    override fun onNumberClicked(keyValue: Int) {
        mPin += keyValue.toString()
        if (isIndicatorDotsAttached) {
            mIndicatorDots!!.updateDot(mPin.length)
        }
        if (mPin.length == 1) {
            mAdapter!!.pinLength = mPin.length
            mAdapter!!.notifyItemChanged(9)
            mAdapter!!.notifyItemChanged(11)
        }
    }

    override fun onConfirmClicked() {
        if (mPinLockListener != null) mPinLockListener!!.onComplete(mPin)
        resetPinLockView()
    }

    override fun onDeleteClicked() {
        if (mPin.isNotEmpty()) {
            mPin = mPin.substring(0, mPin.length - 1)
            if (isIndicatorDotsAttached) mIndicatorDots!!.updateDot(mPin.length)
            if (mPin.isEmpty()) {
                mAdapter!!.pinLength = mPin.length
                mAdapter!!.notifyItemChanged(9)
                mAdapter!!.notifyItemChanged(11)
            }
            if (mPin.isEmpty()) clearInternalPin()
        }
    }

    override fun onDeleteLongClicked() = resetPinLockView()

    fun setPinLockListener(pinLockListener: PinLockListener) {
        this.mPinLockListener = pinLockListener
    }

    fun attachIndicatorDots(mIndicatorDots: IndicatorDots) {
        this.mIndicatorDots = mIndicatorDots
    }

    private fun clearInternalPin() {
        mPin = ""
    }

    private fun resetPinLockView() {
        clearInternalPin()
        mAdapter!!.pinLength = mPin.length
        mAdapter!!.notifyItemChanged(9)
        mAdapter!!.notifyItemChanged(11)

        if (mIndicatorDots != null) mIndicatorDots!!.updateDot(mPin.length)

    }

    class CustomGridLayoutManager(context: Context, spanCount: Int) : GridLayoutManager(context, spanCount) {
        override fun isLayoutRTL(): Boolean {
            return false
        }
    }

}
