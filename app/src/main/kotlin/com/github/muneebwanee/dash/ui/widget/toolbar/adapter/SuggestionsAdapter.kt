package com.github.muneebwanee.dash.ui.widget.toolbar.adapter

import androidx.recyclerview.widget.RecyclerView
import android.widget.Filter
import android.widget.Filterable

/**
 * Created by muneebwanee on 15/12/20..
 */
abstract class SuggestionsAdapter<S, V : RecyclerView.ViewHolder> : RecyclerView.Adapter<V>(), Filterable {

    private var suggestions: MutableList<S> = mutableListOf()
    private var suggestionsClone: MutableList<S> = mutableListOf()

    private  var maxSuggestionsCount = 5

    abstract val singleViewHeight: Int

    val listHeight: Int
        get() = itemCount * singleViewHeight

    fun addSuggestion(r: S?) {
        if (maxSuggestionsCount <= 0)
            return

        if (r == null)
            return
        if (!suggestions.contains(r)) {
            if (suggestions.size >= maxSuggestionsCount) {
                suggestions.removeAt(maxSuggestionsCount - 1)
            }
            suggestions.add(0, r)
        } else {
            suggestions.remove(r)
            suggestions.add(0, r)
        }
        suggestionsClone = suggestions
        notifyDataSetChanged()
    }

    /*fun setSuggestions(suggestions: MutableList<S>) {
        this.suggestions = suggestions
        suggestionsClone = suggestions
        notifyDataSetChanged()
    }*/

    fun deleteSuggestion(position: Int, r: S?) {
        if (r == null) return
        if (suggestions.contains(r)) {
            this.notifyItemRemoved(position)
            suggestions.remove(r)
            suggestionsClone = suggestions
        }
    }

    fun getSuggestions(): MutableList<S> {
        return suggestions
    }

    override fun onBindViewHolder(holder: V, position: Int) {
        onBindSuggestionHolder(suggestions[position], holder, position)
    }

    abstract fun onBindSuggestionHolder(suggestion: S, holder: V, position: Int)

    override fun getItemCount(): Int {
        return suggestions.size
    }

    override fun getFilter(): Filter? {
        return null
    }

    interface OnItemViewClickListener {
        fun onItemClickListener(position: Int, text: String)
        fun onItemDeleteListener(position: Int, text: String)
    }

}
