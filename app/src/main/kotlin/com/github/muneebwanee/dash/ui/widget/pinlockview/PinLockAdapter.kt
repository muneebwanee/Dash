package com.github.muneebwanee.dash.ui.widget.pinlockview

import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.github.muneebwanee.dash.R
import com.github.muneebwanee.dash.utils.ConstFun.setVibrate
import com.pawegio.kandroid.hide
import com.pawegio.kandroid.inflateLayout
import com.pawegio.kandroid.show
import kotterknife.bindView

/**
 * Created by muneebwanee on 15/12/20.
 */
class PinLockAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_NUMBER = 0
    private val VIEW_TYPE_DELETE = 1
    private val VIEW_TYPE_OK = 2

    private var mCustomizationOptionsBundle: CustomizationOptionsBundle? = null
    private var onPinButtonClickListener: OnPinButtonClickListener? = null
    var pinLength: Int = 0

    private val mKeyValues: IntArray = getAdjustKeyValues(intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 0))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_NUMBER -> {
                val view = parent.context.inflateLayout(R.layout.layout_number_item, parent, false)
                NumberViewHolder(view)
            }
            VIEW_TYPE_DELETE -> {
                val view = parent.context.inflateLayout(R.layout.layout_delete_item, parent, false)
                DeleteViewHolder(view)
            }
            else -> {
                val view = parent.context.inflateLayout(R.layout.layout_done_item, parent, false)
                DoneViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when {
            holder.itemViewType == VIEW_TYPE_NUMBER -> {
                val vh1 = holder as NumberViewHolder
                configureNumberButtonHolder(vh1, position)
            }
            holder.itemViewType == VIEW_TYPE_DELETE -> {
                val vh2 = holder as DeleteViewHolder
                configureDeleteButtonHolder(vh2)
            }
            holder.itemViewType == VIEW_TYPE_OK -> {
                val vh3 = holder as DoneViewHolder
                configureDoneButtonHolder(vh3)
            }
        }
    }

    private fun configureNumberButtonHolder(holder: NumberViewHolder?, position: Int) {
        if (holder != null) {
            holder.mNumberButton.text = mKeyValues[position].toString()
            holder.mNumberButton.show()
            holder.mNumberButton.tag = mKeyValues[position]
            if (mCustomizationOptionsBundle != null) {
                holder.mNumberButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, mCustomizationOptionsBundle!!.textSize.toFloat())
                val params = LinearLayout.LayoutParams(mCustomizationOptionsBundle!!.buttonSize, mCustomizationOptionsBundle!!.buttonSize)
                holder.mNumberButton.layoutParams = params
            }
        }
    }

    private fun configureDeleteButtonHolder(holder: DeleteViewHolder?) {
        if (holder != null) {
            if (pinLength > 0) {
                holder.mButtonImage.show()
                if (mCustomizationOptionsBundle != null) {
                    val params = LinearLayout.LayoutParams(mCustomizationOptionsBundle!!.deleteButtonSize, mCustomizationOptionsBundle!!.deleteButtonSize)
                    holder.mButtonImage.layoutParams = params
                }
            } else holder.mButtonImage.hide()
        }
    }

    private fun configureDoneButtonHolder(holder: DoneViewHolder?) {
        if (holder != null) {
            if (pinLength > 0) {
                holder.mButtonImage.isEnabled = true
                holder.mButtonImage.alpha = 1f
                if (mCustomizationOptionsBundle != null) {
                    val params = LinearLayout.LayoutParams(mCustomizationOptionsBundle!!.deleteButtonSize, mCustomizationOptionsBundle!!.deleteButtonSize)
                    holder.mButtonImage.layoutParams = params
                }
            } else{
                holder.mButtonImage.isEnabled = false
                holder.mButtonImage.alpha = 0.3f
            }
        }
    }

    override fun getItemCount(): Int = 12

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            itemCount - 1 -> VIEW_TYPE_OK
            9 -> VIEW_TYPE_DELETE
            else -> VIEW_TYPE_NUMBER
        }
    }

    private fun getAdjustKeyValues(keyValues: IntArray): IntArray {
        val adjustedKeyValues = IntArray(keyValues.size + 1)
        for (i in keyValues.indices) {
            if (i < 9) {
                adjustedKeyValues[i] = keyValues[i]
            } else {
                adjustedKeyValues[i] = -1
                adjustedKeyValues[i + 1] = keyValues[i]
            }
        }
        return adjustedKeyValues
    }

    inner class DoneViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mButtonImage : ImageView by bindView(R.id.buttonImage)
        init {
            if (pinLength > 0){
                mButtonImage.setOnClickListener {
                    itemView.context.setVibrate(50)
                    if (onPinButtonClickListener != null) onPinButtonClickListener!!.onConfirmClicked()
                }
            }
        }
    }

    inner class NumberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val mNumberButton: Button by bindView(R.id.button)
        init {
            mNumberButton.setOnClickListener { v ->
                itemView.context.setVibrate(50)
                if (onPinButtonClickListener != null) onPinButtonClickListener!!.onNumberClicked(v.tag as Int)
            }
        }
    }

    inner class DeleteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mButtonImage: ImageView by bindView(R.id.buttonImage)

        init {
            if (pinLength > 0) {
                mButtonImage.setOnClickListener {
                    itemView.context.setVibrate(50)
                    if (onPinButtonClickListener != null) onPinButtonClickListener!!.onDeleteClicked()
                }
                mButtonImage.setOnLongClickListener {
                    if (onPinButtonClickListener != null) onPinButtonClickListener!!.onDeleteLongClicked()
                    true
                }
            }
        }
    }

    fun setCustomizationOptions(customizationOptionsBundle: CustomizationOptionsBundle) {
        this.mCustomizationOptionsBundle = customizationOptionsBundle
    }

    fun setOnPinButtonClickListener(onPinButtonClickListener: OnPinButtonClickListener) {
        this.onPinButtonClickListener = onPinButtonClickListener
    }

    interface OnPinButtonClickListener {
        fun onNumberClicked(keyValue: Int)
        fun onConfirmClicked()
        fun onDeleteClicked()
        fun onDeleteLongClicked()
    }

}
