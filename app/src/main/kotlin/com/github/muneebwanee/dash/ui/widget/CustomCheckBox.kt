package com.github.muneebwanee.dash.ui.widget

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.graphics.Canvas
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator

/**
 * Created by muneebwanee on 15/12/20.
 */
class CustomCheckBox : View {

    private var circlePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var linePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    internal var isChecked: Boolean = false
        set(value) {
            field = value
            setChecked(value)
        }

    private var radius: Int = 0
    private var w: Int = 0
    private var h: Int = 0
    private var cx: Int = 0
    private var cy: Int = 0
    private var points = FloatArray(6)
    private var correctProgress: Float = 0f
    private var isAnim: Boolean = false

    private var animDuration = 150L
    private var unCheckColor = Color.GRAY
    private var circleColor = Color.RED

    constructor(context: Context) : super(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        circlePaint.style = Paint.Style.FILL
        circlePaint.color = circleColor

        linePaint.style = Paint.Style.FILL
        linePaint.color = Color.WHITE
        linePaint.strokeWidth = 4f
    }


    private fun setChecked(checked: Boolean) {
        if (!checked) {
            hideCorrect()
        } else if (checked) {
            showCheck()
        }
    }

    override fun onSizeChanged(width: Int, height: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(width, height, oldw, oldh)
        w = Math.min(width - paddingLeft - paddingRight, height - paddingBottom - paddingTop)
        h = w
        cx = width / 2
        cy = height / 2

        val r = h / 2f
        points[0] = r / 2f + paddingLeft
        points[1] = r + paddingTop

        points[2] = r * 5f / 6f + paddingLeft
        points[3] = r + r / 3f + paddingTop.toFloat()

        points[4] = r * 1.5f + paddingLeft
        points[5] = r - r / 3f + paddingTop
        radius = (h * 0.125f).toInt()
    }

    override fun onDraw(canvas: Canvas) {

        val f = (radius - h * 0.125f) / (h * 0.5f)
        circlePaint.color = evaluate(f, unCheckColor, circleColor)
        canvas.drawCircle(cx.toFloat(), cy.toFloat(), radius.toFloat(), circlePaint)

        if (correctProgress > 0) {
            if (correctProgress < 1 / 3f) {
                val x = points[0] + (points[2] - points[0]) * correctProgress
                val y = points[1] + (points[3] - points[1]) * correctProgress
                canvas.drawLine(points[0], points[1], x, y, linePaint)
            } else {
                val x = points[2] + (points[4] - points[2]) * correctProgress
                val y = points[3] + (points[5] - points[3]) * correctProgress
                canvas.drawLine(points[0], points[1], points[2], points[3], linePaint)
                canvas.drawLine(points[2], points[3], x, y, linePaint)
            }
        }
    }

    private fun evaluate(fraction: Float, startValue: Int, endValue: Int): Int {
        if (fraction <= 0) {
            return startValue
        }
        if (fraction >= 1) {
            return endValue
        }
        val startA = startValue shr 24 and 0xff
        val startR = startValue shr 16 and 0xff
        val startG = startValue shr 8 and 0xff
        val startB = startValue and 0xff

        val endA = endValue shr 24 and 0xff
        val endR = endValue shr 16 and 0xff
        val endG = endValue shr 8 and 0xff
        val endB = endValue and 0xff

        return startA + (fraction * (endA - startA)).toInt() shl 24 or (startR + (fraction * (endR - startR)).toInt() shl 16) or (startG + (fraction * (endG - startG)).toInt() shl 8) or startB + (fraction * (endB - startB)).toInt()
    }

    private fun showUnChecked() {
        if (isAnim) {
            return
        }

        isAnim = true
        val va = ValueAnimator.ofFloat(0f, 1f).setDuration(animDuration)
        va.interpolator = LinearInterpolator()
        va.start()
        va.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            radius = ((1 - value) * height * 0.375f + height * 0.125f).toInt()
            if (value >= 1) {
                isAnim = false
            }
            invalidate()
        }
    }

    private fun showCheck() {
        if (isAnim) {
            return
        }
        isAnim = true
        val va = ValueAnimator.ofFloat(0f, 1f).setDuration(animDuration)
        va.interpolator = LinearInterpolator()
        va.start()
        va.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            radius = (value * height * 0.37f + height * 0.125f).toInt()
            if (value >= 1) {
                isAnim = false
                showCorrect()
            }
            invalidate()
        }
    }

    private fun showCorrect() {
        if (isAnim) {
            return
        }
        isAnim = true
        val va = ValueAnimator.ofFloat(0f, 1f).setDuration(animDuration)
        va.interpolator = LinearInterpolator()
        va.start()
        va.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            correctProgress = value
            invalidate()
            if (value >= 1) {
                isAnim = false
            }
        }
    }

    private fun hideCorrect() {
        if (isAnim) {
            return
        }
        isAnim = true
        val va = ValueAnimator.ofFloat(0f, 1f).setDuration(animDuration)
        va.interpolator = LinearInterpolator()
        va.start()
        va.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            correctProgress = 1 - value
            invalidate()
            if (value >= 1) {
                isAnim = false
                showUnChecked()
            }
        }
    }
}



