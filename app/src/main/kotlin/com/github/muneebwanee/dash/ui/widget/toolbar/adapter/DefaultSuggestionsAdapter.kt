package com.github.muneebwanee.dash.ui.widget.toolbar.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.github.muneebwanee.dash.R
import com.jakewharton.rxbinding2.view.RxView
import com.pawegio.kandroid.inflateLayout
import kotterknife.bindView

/**
 * Created by muneebwanee on 15/12/20.
 */
class DefaultSuggestionsAdapter : SuggestionsAdapter<String, DefaultSuggestionsAdapter.SuggestionHolder>() {

    private var listener: OnItemViewClickListener? = null

    override val singleViewHeight: Int
        get() = 50

    fun setListener(listener: OnItemViewClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionHolder  =
            SuggestionHolder(parent.context.inflateLayout(R.layout.item_last_request,parent,false))


    @SuppressLint("CheckResult")
    override fun onBindSuggestionHolder(suggestion: String, holder: SuggestionHolder, position: Int) {
        val text = getSuggestions()[position]
        holder.bind(text)
        RxView.clicks(holder.itemView).subscribe {
            listener!!.onItemClickListener(position,text)
        }
        RxView.clicks(holder.ivDelete).subscribe {
            listener!!.onItemDeleteListener(position,text)
        }
    }

    class SuggestionHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        private val text: TextView by bindView(R.id.text)
        val ivDelete: ImageView by bindView(R.id.iv_delete)

        fun bind(text:String){
            this.text.text = text
        }
    }
}
