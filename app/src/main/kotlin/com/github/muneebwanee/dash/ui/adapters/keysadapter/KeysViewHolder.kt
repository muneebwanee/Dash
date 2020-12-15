package com.github.muneebwanee.dash.ui.adapters.keysadapter

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.github.muneebwanee.dash.R
import com.github.muneebwanee.dash.data.model.KeyData
import kotterknife.bindView

/**
 * Created by muneebwanee on 15/12/20.
 */
class KeysViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val txtKeyData: TextView by bindView(R.id.txt_key_text)

    fun bind(item: KeyData) {
        txtKeyData.text = item.keyText
    }

}