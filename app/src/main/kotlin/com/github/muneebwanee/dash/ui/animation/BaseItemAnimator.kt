package com.github.muneebwanee.dash.ui.animation

import android.animation.Animator
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import android.view.View
import androidx.recyclerview.widget.SimpleItemAnimator
import java.util.ArrayList
import kotlin.math.abs

/**
 * Created by muneebwanee on 15/12/20.
 */
abstract class BaseItemAnimator : SimpleItemAnimator() {

    private val mPendingRemovals = ArrayList<ViewHolder>()
    private val mPendingAdditions = ArrayList<ViewHolder>()
    private val mPendingMoves = ArrayList<MoveInfo>()
    private val mPendingChanges = ArrayList<ChangeInfo>()

    private val mAdditionsList = ArrayList<ArrayList<ViewHolder>>()
    private val mMovesList = ArrayList<ArrayList<MoveInfo>>()
    private val mChangesList = ArrayList<ArrayList<ChangeInfo>>()

    private val mAddAnimations = ArrayList<ViewHolder>()
    private val mMoveAnimations = ArrayList<ViewHolder>()
    private val mRemoveAnimations = ArrayList<ViewHolder>()
    private val mChangeAnimations = ArrayList<ViewHolder>()

    private class MoveInfo(var holder: ViewHolder, var fromX: Int, var fromY: Int, var toX: Int, var toY: Int)

    private class ChangeInfo(var oldHolder: ViewHolder?, var newHolder: ViewHolder?) {
        internal var fromX: Int = 0
        internal var fromY: Int = 0
        internal var toX: Int = 0
        internal var toY: Int = 0

        constructor(oldHolder: ViewHolder, newHolder: ViewHolder, fromX: Int, fromY: Int, toX: Int,
                    toY: Int) : this(oldHolder, newHolder) {
            this.fromX = fromX
            this.fromY = fromY
            this.toX = toX
            this.toY = toY
        }

        override fun toString(): String {
            return "ChangeInfo{" +
                    "oldHolder=" + oldHolder +
                    ", newHolder=" + newHolder +
                    ", fromX=" + fromX +
                    ", fromY=" + fromY +
                    ", toX=" + toX +
                    ", toY=" + toY +
                    '}'.toString()
        }
    }

    init {
        supportsChangeAnimations = false
    }

    override fun runPendingAnimations() {
        val removalsPending = mPendingRemovals.isNotEmpty()
        val movesPending = mPendingMoves.isNotEmpty()
        val changesPending = mPendingChanges.isNotEmpty()
        val additionsPending = mPendingAdditions.isNotEmpty()
        if (!removalsPending && !movesPending && !additionsPending && !changesPending) {
            return
        }
        for (holder in mPendingRemovals) {
            doAnimateRemove(holder)
        }
        mPendingRemovals.clear()
        if (movesPending) {
            val moves = ArrayList(mPendingMoves)
            mMovesList.add(moves)
            mPendingMoves.clear()
            val mover = Runnable {
                val removed = mMovesList.remove(moves)
                if (!removed) {
                    return@Runnable
                }
                for (moveInfo in moves) {
                    animateMoveImpl(moveInfo.holder, moveInfo.fromX, moveInfo.fromY, moveInfo.toX,
                            moveInfo.toY)
                }
                moves.clear()
            }
            if (removalsPending) {
                val view = moves[0].holder.itemView
                view.postOnAnimationDelayed(mover, removeDuration)
            } else {
                mover.run()
            }
        }

        if (changesPending) {
            val changes = ArrayList(mPendingChanges)
            mChangesList.add(changes)
            mPendingChanges.clear()
            val changer = Runnable {
                val removed = mChangesList.remove(changes)
                if (!removed) {
                    return@Runnable
                }
                for (change in changes) {
                    animateChangeImpl(change)
                }
                changes.clear()
            }
            if (removalsPending) {
                val holder = changes[0].oldHolder
                holder!!.itemView.postOnAnimationDelayed(changer, removeDuration)
            } else {
                changer.run()
            }
        }

        if (additionsPending) {
            val additions = ArrayList(mPendingAdditions)
            mAdditionsList.add(additions)
            mPendingAdditions.clear()
            val adder = Runnable {
                val removed = mAdditionsList.remove(additions)
                if (!removed) {
                    return@Runnable
                }
                for (holder in additions) {
                    doAnimateAdd(holder)
                }
                additions.clear()
            }
            if (removalsPending || movesPending || changesPending) {
                val removeDuration = if (removalsPending) removeDuration else 0
                val moveDuration = if (movesPending) moveDuration else 0
                val changeDuration = if (changesPending) changeDuration else 0
                val totalDelay = removeDuration + moveDuration.coerceAtLeast(changeDuration)
                val view = additions[0].itemView
                view.postOnAnimationDelayed(adder, totalDelay)
            } else {
                adder.run()
            }
        }
    }

    protected open fun preAnimateRemoveImpl(holder: ViewHolder) {}

    protected open fun preAnimateAddImpl(holder: ViewHolder) {}

    protected abstract fun animateRemoveImpl(holder: ViewHolder)

    protected abstract fun animateAddImpl(holder: ViewHolder)

    private fun preAnimateRemove(holder: ViewHolder) {
        ViewHelper.clear(holder.itemView)

        if (holder is AnimateViewHolder) {
            (holder as AnimateViewHolder).preAnimateRemoveImpl(holder)
        } else {
            preAnimateRemoveImpl(holder)
        }
    }

    private fun preAnimateAdd(holder: ViewHolder) {
        ViewHelper.clear(holder.itemView)

        if (holder is AnimateViewHolder) {
            (holder as AnimateViewHolder).preAnimateAddImpl(holder)
        } else {
            preAnimateAddImpl(holder)
        }
    }

    private fun doAnimateRemove(holder: ViewHolder) {
        if (holder is AnimateViewHolder) {
            (holder as AnimateViewHolder).animateRemoveImpl(holder, DefaultRemoveAnimatorListener(holder))
        } else {
            animateRemoveImpl(holder)
        }

        mRemoveAnimations.add(holder)
    }

    private fun doAnimateAdd(holder: ViewHolder) {
        if (holder is AnimateViewHolder) {
            (holder as AnimateViewHolder).animateAddImpl(holder, DefaultAddAnimatorListener(holder))
        } else {
            animateAddImpl(holder)
        }

        mAddAnimations.add(holder)
    }

    override fun animateRemove(holder: ViewHolder): Boolean {
        endAnimation(holder)
        preAnimateRemove(holder)
        mPendingRemovals.add(holder)
        return true
    }

    internal fun getRemoveDelay(holder: ViewHolder): Long {
        return abs(holder.oldPosition * removeDuration / 4)
    }

    override fun animateAdd(holder: ViewHolder): Boolean {
        endAnimation(holder)
        preAnimateAdd(holder)
        mPendingAdditions.add(holder)
        return true
    }

    internal fun getAddDelay(holder: ViewHolder): Long {
        return abs(holder.adapterPosition * addDuration / 4)
    }

    override fun animateMove(holder: ViewHolder, fromsX: Int, fromsY: Int, toX: Int, toY: Int): Boolean {
        var fromX = fromsX
        var fromY = fromsY
        val view = holder.itemView
        fromX += holder.itemView.translationX.toInt()
        fromY += holder.itemView.translationY.toInt()
        endAnimation(holder)
        val deltaX = toX - fromX
        val deltaY = toY - fromY
        if (deltaX == 0 && deltaY == 0) {
            dispatchMoveFinished(holder)
            return false
        }
        if (deltaX != 0) {
            view.translationX = (-deltaX).toFloat()
        }
        if (deltaY != 0) {
            view.translationY = (-deltaY).toFloat()
        }
        mPendingMoves.add(MoveInfo(holder, fromX, fromY, toX, toY))
        return true
    }

    private fun animateMoveImpl(holder: ViewHolder, fromX: Int, fromY: Int, toX: Int, toY: Int) {
        val view = holder.itemView
        val deltaX = toX - fromX
        val deltaY = toY - fromY
        if (deltaX != 0) {
            view.animate().translationX(0f)
        }
        if (deltaY != 0) {
            view.animate().translationY(0f)
        }
        mMoveAnimations.add(holder)
        val animation = view.animate()
        animation.setDuration(moveDuration).setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                dispatchMoveStarting(holder)
            }

            override fun onAnimationEnd(animations: Animator) {
                animation.setListener(null)
                dispatchMoveFinished(holder)
                mMoveAnimations.remove(holder)
                dispatchFinishedWhenDone()
            }

            override fun onAnimationCancel(animation: Animator) {
                if (deltaX != 0) {
                    view.translationX = 0f
                }
                if (deltaY != 0) {
                    view.translationY = 0f
                }
            }

            override fun onAnimationRepeat(animation: Animator) {}
        }).start()
    }

    override fun animateChange(oldHolder: ViewHolder, newHolder: ViewHolder?, fromX: Int, fromY: Int,
                               toX: Int, toY: Int): Boolean {
        val prevTranslationX = oldHolder.itemView.translationX
        val prevTranslationY = oldHolder.itemView.translationY
        val prevAlpha = oldHolder.itemView.alpha
        endAnimation(oldHolder)
        val deltaX = (toX.toFloat() - fromX.toFloat() - prevTranslationX).toInt()
        val deltaY = (toY.toFloat() - fromY.toFloat() - prevTranslationY).toInt()
        oldHolder.itemView.translationX = prevTranslationX
        oldHolder.itemView.translationY = prevTranslationY
        oldHolder.itemView.alpha = prevAlpha
        if (newHolder?.itemView != null) {
            endAnimation(newHolder)
            newHolder.itemView.translationX = (-deltaX).toFloat()
            newHolder.itemView.translationY = (-deltaY).toFloat()
            newHolder.itemView.alpha = 0f
        }
        mPendingChanges.add(ChangeInfo(oldHolder, newHolder!!, fromX, fromY, toX, toY))
        return true
    }

    private fun animateChangeImpl(changeInfo: ChangeInfo) {
        val holder = changeInfo.oldHolder
        val view = holder?.itemView
        val newHolder = changeInfo.newHolder
        val newView = newHolder?.itemView
        if (view != null) {
            mChangeAnimations.add(changeInfo.oldHolder!!)
            val oldViewAnim = view.animate().setDuration(changeDuration)
            oldViewAnim.translationX((changeInfo.toX - changeInfo.fromX).toFloat())
            oldViewAnim.translationY((changeInfo.toY - changeInfo.fromY).toFloat())
            oldViewAnim.alpha(0f).setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    dispatchChangeStarting(changeInfo.oldHolder, true)
                }

                override fun onAnimationEnd(animation: Animator) {
                    oldViewAnim.setListener(null)
                    view.alpha = 1f
                    view.translationX = 0f
                    view.translationY = 0f
                    dispatchChangeFinished(changeInfo.oldHolder, true)
                    mChangeAnimations.remove(changeInfo.oldHolder!!)
                    dispatchFinishedWhenDone()
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            }).start()
        }

        if (newView != null) {
            mChangeAnimations.add(changeInfo.newHolder!!)
            val newViewAnimation = newView.animate()
            newViewAnimation.translationX(0f).translationY(0f).setDuration(changeDuration).alpha(1f).setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    dispatchChangeStarting(changeInfo.newHolder, false)
                }

                override fun onAnimationEnd(animation: Animator) {
                    newViewAnimation.setListener(null)
                    newView.alpha = 1f
                    newView.translationX = 0f
                    newView.translationY = 0f
                    dispatchChangeFinished(changeInfo.newHolder, false)
                    mChangeAnimations.remove(changeInfo.newHolder!!)
                    dispatchFinishedWhenDone()
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            }).start()
        }
    }

    private fun endChangeAnimation(infoList: MutableList<ChangeInfo>, item: ViewHolder) {
        for (i in infoList.indices.reversed()) {
            val changeInfo = infoList[i]
            if (endChangeAnimationIfNecessary(changeInfo, item)) {
                if (changeInfo.oldHolder == null && changeInfo.newHolder == null) {
                    infoList.remove(changeInfo)
                }
            }
        }
    }

    private fun endChangeAnimationIfNecessary(changeInfo: ChangeInfo) {
        if (changeInfo.oldHolder != null) {
            endChangeAnimationIfNecessary(changeInfo, changeInfo.oldHolder)
        }
        if (changeInfo.newHolder != null) {
            endChangeAnimationIfNecessary(changeInfo, changeInfo.newHolder)
        }
    }

    private fun endChangeAnimationIfNecessary(changeInfo: ChangeInfo, item: ViewHolder?): Boolean {
        var oldItem = false
        when {
            changeInfo.newHolder === item -> changeInfo.newHolder = null
            changeInfo.oldHolder === item -> {
                changeInfo.oldHolder = null
                oldItem = true
            }
            else -> return false
        }
        item!!.itemView.alpha = 1f
        item.itemView.translationX = 0f
        item.itemView.translationY = 0f
        dispatchChangeFinished(item, oldItem)
        return true
    }

    override fun endAnimation(item: ViewHolder) {
        val view = item.itemView
        view.animate().cancel()
        for (i in mPendingMoves.indices.reversed()) {
            val moveInfo = mPendingMoves[i]
            if (moveInfo.holder === item) {
                view.translationY = 0f
                view.translationX = 0f
                dispatchMoveFinished(item)
                mPendingMoves.removeAt(i)
            }
        }
        endChangeAnimation(mPendingChanges, item)
        if (mPendingRemovals.remove(item)) {
            ViewHelper.clear(item.itemView)
            dispatchRemoveFinished(item)
        }
        if (mPendingAdditions.remove(item)) {
            ViewHelper.clear(item.itemView)
            dispatchAddFinished(item)
        }

        for (i in mChangesList.indices.reversed()) {
            val changes = mChangesList[i]
            endChangeAnimation(changes, item)
            if (changes.isEmpty()) {
                mChangesList.removeAt(i)
            }
        }
        for (i in mMovesList.indices.reversed()) {
            val moves = mMovesList[i]
            for (j in moves.indices.reversed()) {
                val moveInfo = moves[j]
                if (moveInfo.holder === item) {
                    view.translationY = 0f
                    view.translationX = 0f
                    dispatchMoveFinished(item)
                    moves.removeAt(j)
                    if (moves.isEmpty()) {
                        mMovesList.removeAt(i)
                    }
                    break
                }
            }
        }
        for (i in mAdditionsList.indices.reversed()) {
            val additions = mAdditionsList[i]
            if (additions.remove(item)) {
                ViewHelper.clear(item.itemView)
                dispatchAddFinished(item)
                if (additions.isEmpty()) {
                    mAdditionsList.removeAt(i)
                }
            }
        }

        check(!(mRemoveAnimations.remove(item) && DEBUG)) { "after animation is cancelled, item should not be in " + "mRemoveAnimations list" }
        check(!(mAddAnimations.remove(item) && DEBUG)) { "after animation is cancelled, item should not be in " + "mAddAnimations list" }
        check(!(mChangeAnimations.remove(item) && DEBUG)) { "after animation is cancelled, item should not be in " + "mChangeAnimations list" }
        check(!(mMoveAnimations.remove(item) && DEBUG)) { "after animation is cancelled, item should not be in " + "mMoveAnimations list" }

        dispatchFinishedWhenDone()
    }

    override fun isRunning(): Boolean {
        return mPendingAdditions.isNotEmpty() ||
                mPendingChanges.isNotEmpty() ||
                mPendingMoves.isNotEmpty() ||
                mPendingRemovals.isNotEmpty() ||
                mMoveAnimations.isNotEmpty() ||
                mRemoveAnimations.isNotEmpty() ||
                mAddAnimations.isNotEmpty() ||
                mChangeAnimations.isNotEmpty() ||
                mMovesList.isNotEmpty() ||
                mAdditionsList.isNotEmpty() ||
                mChangesList.isNotEmpty()
    }

    private fun dispatchFinishedWhenDone() {
        if (!isRunning) {
            dispatchAnimationsFinished()
        }
    }

    override fun endAnimations() {
        var count = mPendingMoves.size
        for (i in count - 1 downTo 0) {
            val item = mPendingMoves[i]
            val view = item.holder.itemView
            view.translationY = 0f
            view.translationX = 0f
            dispatchMoveFinished(item.holder)
            mPendingMoves.removeAt(i)
        }
        count = mPendingRemovals.size
        for (i in count - 1 downTo 0) {
            val item = mPendingRemovals[i]
            dispatchRemoveFinished(item)
            mPendingRemovals.removeAt(i)
        }
        count = mPendingAdditions.size
        for (i in count - 1 downTo 0) {
            val item = mPendingAdditions[i]
            ViewHelper.clear(item.itemView)
            dispatchAddFinished(item)
            mPendingAdditions.removeAt(i)
        }
        count = mPendingChanges.size
        for (i in count - 1 downTo 0) {
            endChangeAnimationIfNecessary(mPendingChanges[i])
        }
        mPendingChanges.clear()
        if (!isRunning) {
            return
        }

        var listCount = mMovesList.size
        for (i in listCount - 1 downTo 0) {
            val moves = mMovesList[i]
            count = moves.size
            for (j in count - 1 downTo 0) {
                val moveInfo = moves[j]
                val item = moveInfo.holder
                val view = item.itemView
                view.translationY = 0f
                view.translationX = 0f
                dispatchMoveFinished(moveInfo.holder)
                moves.removeAt(j)
                if (moves.isEmpty()) {
                    mMovesList.remove(moves)
                }
            }
        }
        listCount = mAdditionsList.size
        for (i in listCount - 1 downTo 0) {
            val additions = mAdditionsList[i]
            count = additions.size
            for (j in count - 1 downTo 0) {
                val item = additions[j]
                val view = item.itemView
                view.alpha = 1f
                dispatchAddFinished(item)
                if (j < additions.size) {
                    additions.removeAt(j)
                }
                if (additions.isEmpty()) {
                    mAdditionsList.remove(additions)
                }
            }
        }
        listCount = mChangesList.size
        for (i in listCount - 1 downTo 0) {
            val changes = mChangesList[i]
            count = changes.size
            for (j in count - 1 downTo 0) {
                endChangeAnimationIfNecessary(changes[j])
                if (changes.isEmpty()) {
                    mChangesList.remove(changes)
                }
            }
        }

        cancelAll(mRemoveAnimations)
        cancelAll(mMoveAnimations)
        cancelAll(mAddAnimations)
        cancelAll(mChangeAnimations)

        dispatchAnimationsFinished()
    }

    private fun cancelAll(viewHolders: List<ViewHolder>) {
        for (i in viewHolders.indices.reversed()) {
            viewHolders[i].itemView.animate().cancel()
        }
    }

    protected inner class DefaultAddAnimatorListener(private var mViewHolder: ViewHolder) : Animator.AnimatorListener {

        override fun onAnimationStart(animation: Animator) {
            dispatchAddStarting(mViewHolder)
        }

        override fun onAnimationEnd(animation: Animator) {
            ViewHelper.clear(mViewHolder.itemView)
            dispatchAddFinished(mViewHolder)
            mAddAnimations.remove(mViewHolder)
            dispatchFinishedWhenDone()
        }

        override fun onAnimationCancel(animation: Animator) {
            ViewHelper.clear(mViewHolder.itemView)
        }

        override fun onAnimationRepeat(animation: Animator) {}
    }

    protected inner class DefaultRemoveAnimatorListener(private var mViewHolder: ViewHolder) : Animator.AnimatorListener {

        override fun onAnimationStart(animation: Animator) {
            dispatchRemoveStarting(mViewHolder)
        }

        override fun onAnimationCancel(animation: Animator) {
            ViewHelper.clear(mViewHolder.itemView)
        }

        override fun onAnimationEnd(animation: Animator) {
            ViewHelper.clear(mViewHolder.itemView)
            dispatchRemoveFinished(mViewHolder)
            mRemoveAnimations.remove(mViewHolder)
            dispatchFinishedWhenDone()
        }

        override fun onAnimationRepeat(animation: Animator) {}
    }

    interface AnimateViewHolder {

        fun preAnimateAddImpl(holder: ViewHolder)

        fun preAnimateRemoveImpl(holder: ViewHolder)

        fun animateAddImpl(holder: ViewHolder, listener: Animator.AnimatorListener)

        fun animateRemoveImpl(holder: ViewHolder, listener: Animator.AnimatorListener)
    }

    object ViewHelper {
        fun clear(v: View) {
            v.alpha = 1f
            v.scaleY = 1f
            v.scaleX = 1f
            v.translationY = 0f
            v.translationX = 0f
            v.rotation = 0f
            v.rotationY = 0f
            v.rotationX = 0f
            v.pivotY = (v.measuredHeight / 2).toFloat()
            v.pivotX = (v.measuredWidth / 2).toFloat()
            v.animate().setInterpolator(null).startDelay = 0
        }
    }

    companion object {
        private const val DEBUG = false
    }
}
