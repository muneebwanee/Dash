package com.github.muneebwanee.dash.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.widget.AppCompatImageView
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.OverScroller
import kotlin.math.abs

/**
 * Created by muneebwanee on 15/12/20.
 */
class TouchImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : AppCompatImageView(context, attrs, defStyle) {


    internal var matrix: Matrix? = null
    private var prevMatrix: Matrix? = null

    internal var m: FloatArray? = null

    private var state: State? = null

    private var fling: Fling? = null

    internal var viewWidth: Int = 0
    internal var viewHeight: Int = 0
    private var prevViewWidth: Int = 0
    private var prevViewHeight: Int = 0

    var currentZoom: Float = 0.toFloat()
        internal set
    internal var minScale: Float = 0.toFloat()
    internal var maxScale: Float = 0.toFloat()
    private var superMinScale: Float = 0.toFloat()
    private var superMaxScale: Float = 0.toFloat()

    internal var gestureDetector: GestureDetector? = null
    internal var onTouchListener: OnTouchListener? = null
    internal var scaleGestureDetector: ScaleGestureDetector? = null
    internal var touchImageViewListener: OnTouchImageViewListener? = null
    internal var doubleTapListener: GestureDetector.OnDoubleTapListener? = null

    private var scaleType: ScaleType? = null
    private var delayedZoomVariables: ZoomVariables? = null
    private var isImageRenderedAtLeastOnce: Boolean = false
    private var onDrawReady: Boolean = false

    private var matchViewWidth: Float = 0.toFloat()
    private var matchViewHeight: Float = 0.toFloat()
    private var prevMatchViewWidth: Float = 0.toFloat()
    private var prevMatchViewHeight: Float = 0.toFloat()

    private val isZoomed: Boolean
        get() = currentZoom != 1f

    private val scrollPosition: PointF?
        get() {
            val drawable = drawable ?: return null

            val drawableWidth = drawable.intrinsicWidth
            val drawableHeight = drawable.intrinsicHeight

            val point = transformCordTouchToBitmap((viewWidth / 2).toFloat(), (viewHeight / 2).toFloat(), true)
            point.x /= drawableWidth.toFloat()
            point.y /= drawableHeight.toFloat()
            return point
        }

    internal val imageWidth: Float
        get() = matchViewWidth * currentZoom

    internal val imageHeight: Float
        get() = matchViewHeight * currentZoom

    init {
        configureImageView(context)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun configureImageView(context: Context) {
        super.setClickable(true)

        scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
        gestureDetector = GestureDetector(context, GestureListener())

        matrix = Matrix()
        prevMatrix = Matrix()

        m = FloatArray(9)
        currentZoom = 1f
        if (scaleType == null) {
            scaleType = ScaleType.FIT_CENTER
        }

        minScale = 1f
        maxScale = 3f

        superMinScale = SUPER_MIN_MULTIPLIER * minScale
        superMaxScale = SUPER_MAX_MULTIPLIER * maxScale

        imageMatrix = matrix
        setScaleType(ScaleType.MATRIX)
        setState(State.NONE)
        onDrawReady = false
        super.setOnTouchListener(PrivateOnTouchListener())
    }

    override fun setOnTouchListener(l: OnTouchListener) {
        onTouchListener = l
    }

    override fun setImageResource(resId: Int) {
        isImageRenderedAtLeastOnce = false
        super.setImageResource(resId)
        savePreviousImageValues()
        fitImageToView()
    }

    override fun setImageBitmap(bm: Bitmap) {
        isImageRenderedAtLeastOnce = false
        super.setImageBitmap(bm)
        savePreviousImageValues()
        fitImageToView()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        isImageRenderedAtLeastOnce = false
        super.setImageDrawable(drawable)
        savePreviousImageValues()
        fitImageToView()
    }

    override fun setImageURI(uri: Uri?) {
        isImageRenderedAtLeastOnce = false
        super.setImageURI(uri)
        savePreviousImageValues()
        fitImageToView()
    }

    override fun getScaleType(): ScaleType? {
        return scaleType
    }

    override fun setScaleType(type: ScaleType?) {
        if (type == ScaleType.FIT_START || type == ScaleType.FIT_END) {
            throw UnsupportedOperationException("TouchImageView does not support FIT_START or FIT_END")
        }

        if (type == ScaleType.MATRIX) {
            super.setScaleType(ScaleType.MATRIX)
        } else {
            scaleType = type
            if (onDrawReady) {
                setZoom(this)
            }
        }
    }

    private fun savePreviousImageValues() {
        if (matrix != null && viewHeight != 0 && viewWidth != 0) {
            matrix!!.getValues(m)
            prevMatrix!!.setValues(m)
            prevMatchViewHeight = matchViewHeight
            prevMatchViewWidth = matchViewWidth
            prevViewHeight = viewHeight
            prevViewWidth = viewWidth
        }
    }

    public override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable("instanceState", super.onSaveInstanceState())
        bundle.putFloat("saveScale", currentZoom)
        bundle.putFloat("matchViewHeight", matchViewHeight)
        bundle.putFloat("matchViewWidth", matchViewWidth)
        bundle.putInt("viewWidth", viewWidth)
        bundle.putInt("viewHeight", viewHeight)
        matrix!!.getValues(m)
        bundle.putFloatArray("matrix", m)
        bundle.putBoolean("imageRendered", isImageRenderedAtLeastOnce)
        return bundle
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            val bundle = state
            currentZoom = bundle.getFloat("saveScale")
            m = bundle.getFloatArray("matrix")
            prevMatrix!!.setValues(m)
            prevMatchViewHeight = bundle.getFloat("matchViewHeight")
            prevMatchViewWidth = bundle.getFloat("matchViewWidth")
            prevViewHeight = bundle.getInt("viewHeight")
            prevViewWidth = bundle.getInt("viewWidth")
            isImageRenderedAtLeastOnce = bundle.getBoolean("imageRendered")
            super.onRestoreInstanceState(bundle.getParcelable("instanceState"))
            return
        }
        super.onRestoreInstanceState(state)
    }

    override fun onDraw(canvas: Canvas) {
        onDrawReady = true
        isImageRenderedAtLeastOnce = true
        if (delayedZoomVariables != null) {
            setZoom(delayedZoomVariables!!.scale, delayedZoomVariables!!.focusX,
                    delayedZoomVariables!!.focusY, delayedZoomVariables!!.scaleType)
            delayedZoomVariables = null
        }
        super.onDraw(canvas)
    }

    public override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        savePreviousImageValues()
    }

    private fun resetZoom() {
        currentZoom = 1f
        fitImageToView()
    }

    private fun setZoom(scale: Float, focusX: Float, focusY: Float, scaleType: ScaleType?) {
        if (!onDrawReady) {
            delayedZoomVariables = ZoomVariables(scale, focusX, focusY, scaleType!!)
            return
        }

        if (scaleType != this.scaleType) {
            setScaleType(scaleType)
        }

        resetZoom()
        scaleImage(scale.toDouble(), (viewWidth / 2).toFloat(), (viewHeight / 2).toFloat(), true)
        matrix!!.getValues(m)
        m?.set(Matrix.MTRANS_X, -(focusX * imageWidth - viewWidth * 0.5f))
        m?.set(Matrix.MTRANS_Y, -(focusY * imageHeight - viewHeight * 0.5f))
        matrix!!.setValues(m)
        fixTrans()
        imageMatrix = matrix
    }

    private fun setZoom(img: TouchImageView) {
        val center = img.scrollPosition
        if (center != null) {
            setZoom(img.currentZoom, center.x, center.y, img.getScaleType())
        }
    }

    internal fun fixTrans() {
        matrix!!.getValues(m)
        val transX = m!![Matrix.MTRANS_X]
        val transY = m!![Matrix.MTRANS_Y]

        val fixTransX = getFixTrans(transX, viewWidth.toFloat(), imageWidth)
        val fixTransY = getFixTrans(transY, viewHeight.toFloat(), imageHeight)

        if (fixTransX != 0f || fixTransY != 0f) {
            matrix!!.postTranslate(fixTransX, fixTransY)
        }
    }

    internal fun fixScaleTrans() {
        fixTrans()
        matrix!!.getValues(m)
        if (imageWidth < viewWidth) {
            m?.set(Matrix.MTRANS_X, (viewWidth - imageWidth) / 2)
        }

        if (imageHeight < viewHeight) {
            m?.set(Matrix.MTRANS_Y, (viewHeight - imageHeight) / 2)
        }
        matrix!!.setValues(m)
    }

    private fun getFixTrans(trans: Float, viewSize: Float, contentSize: Float): Float {
        val minTrans: Float
        val maxTrans: Float

        if (contentSize <= viewSize) {
            minTrans = 0f
            maxTrans = viewSize - contentSize
        } else {
            minTrans = viewSize - contentSize
            maxTrans = 0f
        }

        if (trans < minTrans) {
            return -trans + minTrans
        }
        return if (trans > maxTrans) {
            -trans + maxTrans
        } else 0f
    }

    internal fun getFixDragTrans(delta: Float, viewSize: Float, contentSize: Float): Float {
        return if (contentSize <= viewSize) {
            0f
        } else delta
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val drawable = drawable
        if (drawable == null || drawable.intrinsicWidth == 0 || drawable.intrinsicHeight == 0) {
            setMeasuredDimension(0, 0)
            return
        }

        val drawableWidth = drawable.intrinsicWidth
        val drawableHeight = drawable.intrinsicHeight
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        viewWidth = setViewSize(widthMode, widthSize, drawableWidth)

        viewHeight = setViewSize(heightMode, heightSize, drawableHeight)

        setMeasuredDimension(viewWidth, viewHeight)

        fitImageToView()
    }

    private fun fitImageToView() {
        val drawable = drawable
        if (drawable == null || drawable.intrinsicWidth == 0 || drawable.intrinsicHeight == 0) {
            return
        }

        if (matrix == null || prevMatrix == null) {
            return
        }

        val drawableWidth = drawable.intrinsicWidth
        val drawableHeight = drawable.intrinsicHeight

        var scaleX = viewWidth.toFloat() / drawableWidth
        var scaleY = viewHeight.toFloat() / drawableHeight

        when (scaleType) {
            ScaleType.CENTER -> {
                scaleY = 1f
                scaleX = scaleY
            }

            ScaleType.CENTER_CROP -> {
                scaleY = scaleX.coerceAtLeast(scaleY)
                scaleX = scaleY
            }

            ScaleType.CENTER_INSIDE -> {
                scaleY = 1f.coerceAtMost(scaleX.coerceAtMost(scaleY))
                scaleX = scaleY
            }

            ScaleType.FIT_CENTER -> {
                scaleY = scaleX.coerceAtMost(scaleY)
                scaleX = scaleY
            }

            ScaleType.FIT_XY -> {
            }

            else -> throw UnsupportedOperationException("TouchImageView does not support " + "FIT_START or FIT_END")
        }

        val redundantXSpace = viewWidth - scaleX * drawableWidth
        val redundantYSpace = viewHeight - scaleY * drawableHeight
        matchViewWidth = viewWidth - redundantXSpace
        matchViewHeight = viewHeight - redundantYSpace
        if (!isZoomed && !isImageRenderedAtLeastOnce) {
            matrix!!.setScale(scaleX, scaleY)
            matrix!!.postTranslate(redundantXSpace / 2, redundantYSpace / 2)
            currentZoom = 1f

        } else {
            savePreviousImageValues()

            prevMatrix!!.getValues(m)

            m?.set(Matrix.MSCALE_X, matchViewWidth / drawableWidth * currentZoom)
            m?.set(Matrix.MSCALE_Y, matchViewHeight / drawableHeight * currentZoom)

            val transX = m!![Matrix.MTRANS_X]
            val transY = m!![Matrix.MTRANS_Y]

            val prevActualWidth = prevMatchViewWidth * currentZoom
            val actualWidth = imageWidth
            translateMatrixAfterRotate(Matrix.MTRANS_X, transX, prevActualWidth, actualWidth, prevViewWidth,
                    viewWidth, drawableWidth)

            val prevActualHeight = prevMatchViewHeight * currentZoom
            val actualHeight = imageHeight
            translateMatrixAfterRotate(Matrix.MTRANS_Y, transY, prevActualHeight, actualHeight, prevViewHeight,
                    viewHeight, drawableHeight)

            matrix!!.setValues(m)
        }
        fixTrans()
        imageMatrix = matrix
    }

    private fun setViewSize(mode: Int, size: Int, drawableWidth: Int): Int {
        return when (mode) {
            MeasureSpec.EXACTLY -> size

            MeasureSpec.AT_MOST -> drawableWidth.coerceAtMost(size)

            MeasureSpec.UNSPECIFIED -> drawableWidth

            else -> size
        }
    }

    private fun translateMatrixAfterRotate(axis: Int, trans: Float, prevImageSize: Float, imageSize: Float,
                                           prevViewSize: Int, viewSize: Int, drawableSize: Int) {
        when {
            imageSize < viewSize -> m?.set(axis, (viewSize - drawableSize * m!![Matrix.MSCALE_X]) * 0.5f)
            trans > 0 -> m?.set(axis, -((imageSize - viewSize) * 0.5f))
            else -> {
                val percentage = (abs(trans) + 0.5f * prevViewSize) / prevImageSize
                m?.set(axis, -(percentage * imageSize - viewSize * 0.5f))
            }
        }
    }

    private fun setState(state: State) {
        this.state = state
    }

    override fun canScrollHorizontally(direction: Int): Boolean {
        matrix!!.getValues(m)
        val x = m!![Matrix.MTRANS_X]

        if (imageWidth < viewWidth) {
            return false
        } else if (x >= -1 && direction < 0) {
            return false
        } else if (abs(x) + viewWidth.toFloat() + 1f >= imageWidth && direction > 0) {
            return false
        }
        return true
    }

    internal fun scaleImage(deltaScale: Double, focusX: Float, focusY: Float, stretchImageToSuper: Boolean) {
        var deltaScales = deltaScale
        val lowerScale: Float
        val upperScale: Float
        if (stretchImageToSuper) {
            lowerScale = superMinScale
            upperScale = superMaxScale
        } else {
            lowerScale = minScale
            upperScale = maxScale
        }

        val origScale = currentZoom
        currentZoom *= deltaScales.toFloat()
        if (currentZoom > upperScale) {
            currentZoom = upperScale
            deltaScales = (upperScale / origScale).toDouble()
        } else if (currentZoom < lowerScale) {
            currentZoom = lowerScale
            deltaScales = (lowerScale / origScale).toDouble()
        }

        matrix!!.postScale(deltaScales.toFloat(), deltaScales.toFloat(), focusX, focusY)
        fixScaleTrans()
    }

    internal fun transformCordTouchToBitmap(x: Float, y: Float, clipToBitmap: Boolean): PointF {
        matrix!!.getValues(m)
        val origW = drawable.intrinsicWidth.toFloat()
        val origH = drawable.intrinsicHeight.toFloat()
        val transX = m!![Matrix.MTRANS_X]
        val transY = m!![Matrix.MTRANS_Y]
        var finalX = (x - transX) * origW / imageWidth
        var finalY = (y - transY) * origH / imageHeight

        if (clipToBitmap) {
            finalX = finalX.coerceAtLeast(0f).coerceAtMost(origW)
            finalY = finalY.coerceAtLeast(0f).coerceAtMost(origH)
        }

        return PointF(finalX, finalY)
    }

    internal fun transformCordBitmapToTouch(bx: Float, by: Float): PointF {
        matrix!!.getValues(m)
        val origW = drawable.intrinsicWidth.toFloat()
        val origH = drawable.intrinsicHeight.toFloat()
        val px = bx / origW
        val py = by / origH
        val finalX = m!![Matrix.MTRANS_X] + imageWidth * px
        val finalY = m!![Matrix.MTRANS_Y] + imageHeight * py
        return PointF(finalX, finalY)
    }

    private enum class State {
        NONE, DRAG, ZOOM, FLING, ANIMATE_ZOOM
    }

    internal interface OnTouchImageViewListener {
        fun onMove()
    }

    private class CompatScroller internal constructor(context: Context) {

        internal val overScroller: OverScroller = OverScroller(context)

        internal val isFinished: Boolean
            get() = overScroller.isFinished

        internal val currX: Int
            get() = overScroller.currX

        internal val currY: Int
            get() = overScroller.currY

        internal fun fling(startX: Int, startY: Int, velocityX: Int, velocityY: Int, minX: Int, maxX: Int, minY: Int, maxY: Int) {
            overScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY)
        }

        internal fun forceFinished(finished: Boolean) {
            overScroller.forceFinished(finished)
        }

        internal fun computeScrollOffset(): Boolean {
            return overScroller.computeScrollOffset()
        }
    }

    private class ZoomVariables internal constructor(internal val scale: Float, internal val focusX: Float, internal val focusY: Float, internal val scaleType: ScaleType)

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            return if (doubleTapListener != null) {
                doubleTapListener!!.onSingleTapConfirmed(e)
            } else performClick()
        }

        override fun onLongPress(e: MotionEvent) {
            performLongClick()
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            if (fling != null) {
                fling!!.cancelFling()
            }

            fling = Fling(velocityX.toInt(), velocityY.toInt())
            postOnAnimation(fling)
            return super.onFling(e1, e2, velocityX, velocityY)
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            var consumed = false
            if (doubleTapListener != null) {
                consumed = doubleTapListener!!.onDoubleTap(e)
            }

            if (state == State.NONE) {
                val targetZoom = if (currentZoom == minScale) maxScale else minScale
                val doubleTap = DoubleTapZoom(targetZoom, e.x, e.y, false)
                postOnAnimation(doubleTap)
                consumed = true
            }
            return consumed
        }

        override fun onDoubleTapEvent(e: MotionEvent): Boolean {
            return doubleTapListener != null && doubleTapListener!!.onDoubleTapEvent(e)
        }
    }

    private inner class PrivateOnTouchListener : OnTouchListener {

        private val last = PointF()

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            scaleGestureDetector!!.onTouchEvent(event)
            gestureDetector!!.onTouchEvent(event)
            val curr = PointF(event.x, event.y)

            if (state == State.NONE || state == State.DRAG || state == State.FLING) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        last.set(curr)
                        if (fling != null) {
                            fling!!.cancelFling()
                        }
                        setState(State.DRAG)
                    }

                    MotionEvent.ACTION_MOVE -> if (state == State.DRAG) {
                        val deltaX = curr.x - last.x
                        val deltaY = curr.y - last.y
                        val fixTransX = getFixDragTrans(deltaX, viewWidth.toFloat(), imageWidth)
                        val fixTransY = getFixDragTrans(deltaY, viewHeight.toFloat(), imageHeight)
                        matrix!!.postTranslate(fixTransX, fixTransY)
                        fixTrans()
                        last.set(curr.x, curr.y)
                    }

                    MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> setState(State.NONE)
                }
            }

            imageMatrix = matrix

            if (onTouchListener != null) {
                onTouchListener!!.onTouch(v, event)
            }

            if (touchImageViewListener != null) {
                touchImageViewListener!!.onMove()
            }

            return true
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            setState(State.ZOOM)
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleImage(detector.scaleFactor.toDouble(), detector.focusX, detector.focusY, true)

            if (touchImageViewListener != null) {
                touchImageViewListener!!.onMove()
            }
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            super.onScaleEnd(detector)
            setState(State.NONE)
            var animateToZoomBoundary = false
            var targetZoom = currentZoom
            if (currentZoom > maxScale) {
                targetZoom = maxScale
                animateToZoomBoundary = true

            } else if (currentZoom < minScale) {
                targetZoom = minScale
                animateToZoomBoundary = true
            }

            if (animateToZoomBoundary) {
                val doubleTap = DoubleTapZoom(targetZoom, (viewWidth / 2).toFloat(), (viewHeight / 2).toFloat(), true)
                postOnAnimation(doubleTap)
            }
        }
    }

    private inner class DoubleTapZoom internal constructor(private val targetZoom: Float, focusX: Float, focusY: Float, private val stretchImageToSuper: Boolean) : Runnable {

        private val startTime: Long
        private val startTouch: PointF
        private val endTouch: PointF
        private val bitmapX: Float
        private val bitmapY: Float
        private val startZoom: Float
        private val interpolator = AccelerateDecelerateInterpolator()

        init {
            setState(State.ANIMATE_ZOOM)
            startTime = System.currentTimeMillis()
            this.startZoom = currentZoom

            val bitmapPoint = transformCordTouchToBitmap(focusX, focusY, false)
            bitmapX = bitmapPoint.x
            bitmapY = bitmapPoint.y

            startTouch = transformCordBitmapToTouch(bitmapX, bitmapY)
            endTouch = PointF((viewWidth / 2).toFloat(), (viewHeight / 2).toFloat())
        }

        override fun run() {
            val t = interpolate()
            val deltaScale = calculateDeltaScale(t)
            scaleImage(deltaScale, bitmapX, bitmapY, stretchImageToSuper)
            translateImageToCenterTouchPosition(t)
            fixScaleTrans()
            imageMatrix = matrix

            if (touchImageViewListener != null) {
                touchImageViewListener!!.onMove()
            }

            if (t < 1f) {
                postOnAnimation(this)
            } else {
                setState(State.NONE)
            }
        }

        private fun translateImageToCenterTouchPosition(t: Float) {
            val targetX = startTouch.x + t * (endTouch.x - startTouch.x)
            val targetY = startTouch.y + t * (endTouch.y - startTouch.y)
            val curr = transformCordBitmapToTouch(bitmapX, bitmapY)
            matrix!!.postTranslate(targetX - curr.x, targetY - curr.y)
        }

        private fun interpolate(): Float {
            val currTime = System.currentTimeMillis()
            var elapsed = (currTime - startTime) / 500f
            elapsed = 1f.coerceAtMost(elapsed)
            return interpolator.getInterpolation(elapsed)
        }

        private fun calculateDeltaScale(t: Float): Double {
            val zoom = (startZoom + t * (targetZoom - startZoom)).toDouble()
            return zoom / currentZoom
        }
    }

    private inner class Fling internal constructor(velocityX: Int, velocityY: Int) : Runnable {

        internal var currX: Int = 0
        internal var currY: Int = 0
        internal var scroller: CompatScroller? = null

        init {
            setState(State.FLING)
            scroller = CompatScroller(context)
            matrix!!.getValues(m)

            val startX = m!![Matrix.MTRANS_X].toInt()
            val startY = m!![Matrix.MTRANS_Y].toInt()
            val minX: Int
            val maxX: Int
            val minY: Int
            val maxY: Int

            if (imageWidth > viewWidth) {
                minX = viewWidth - imageWidth.toInt()
                maxX = 0
            } else {
                maxX = startX
                minX = maxX
            }

            if (imageHeight > viewHeight) {
                minY = viewHeight - imageHeight.toInt()
                maxY = 0
            } else {
                maxY = startY
                minY = maxY
            }

            scroller!!.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY)
            currX = startX
            currY = startY
        }

        internal fun cancelFling() {
            if (scroller != null) {
                setState(State.NONE)
                scroller!!.forceFinished(true)
            }
        }

        override fun run() {
            if (touchImageViewListener != null) {
                touchImageViewListener!!.onMove()
            }

            if (scroller!!.isFinished) {
                scroller = null
                return
            }

            if (scroller!!.computeScrollOffset()) {
                val newX = scroller!!.currX
                val newY = scroller!!.currY
                val transX = newX - currX
                val transY = newY - currY
                currX = newX
                currY = newY
                matrix!!.postTranslate(transX.toFloat(), transY.toFloat())
                fixTrans()
                imageMatrix = matrix
                postOnAnimation(this)
            }
        }
    }

    companion object {
        private const val SUPER_MIN_MULTIPLIER = .75f
        private const val SUPER_MAX_MULTIPLIER = 1.25f
    }
}