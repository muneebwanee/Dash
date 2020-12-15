package com.github.muneebwanee.dash.ui.adapters.notifyadapter

import androidx.core.content.ContextCompat
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.github.muneebwanee.dash.R
import com.github.muneebwanee.dash.data.preference.DataSharePreference.getSelectedItem
import com.github.muneebwanee.dash.data.model.NotificationMessages
import com.github.muneebwanee.dash.utils.ConstFun.setImageUrl
import com.github.muneebwanee.dash.utils.ConstFun.setImageId
import com.github.muneebwanee.dash.utils.Consts.TYPE_INSTAGRAM
import com.github.muneebwanee.dash.utils.Consts.TYPE_MESSENGER
import com.github.muneebwanee.dash.utils.Consts.TYPE_WHATSAPP
import de.hdodenhof.circleimageview.CircleImageView
import kotterknife.bindView

class NotifyMessageViewHolder(view: View) : RecyclerView.ViewHolder(view){

    val itemClick : RelativeLayout by bindView(R.id.item_click_notify)
    private val iconType : ImageView by bindView(R.id.icon_type_notification)
    private val textType : TextView by bindView(R.id.text_type_notification)
    private val dateNotify : TextView by bindView(R.id.date_notification)
    private val titleNotify : TextView by bindView(R.id.title_notification)
    private val textNotify : TextView by bindView(R.id.text_notification)
    private val imgNotify : CircleImageView by bindView(R.id.img_notification)
    private val card : CardView by bindView(R.id.cardview_notify)

    fun isSelectedItem(key:String,urlImage:String?){
        if (itemView.context.getSelectedItem(key)){
            card.setCardBackgroundColor(ContextCompat.getColor(itemView.context,R.color.colorSelected))
            imgNotify.setImageId(R.drawable.ic_check)
        }
        else {
            card.setCardBackgroundColor(ContextCompat.getColor(itemView.context,R.color.colorWhite))
            if (!urlImage.isNullOrEmpty()) imgNotify.setImageUrl(urlImage,R.drawable.ic_placeholder_profile)
        }
    }

    fun bind(item:NotificationMessages){
        dateNotify.text = item.dateTime
        titleNotify.text = item.title
        textNotify.text = item.text

        when(item.type) {
            TYPE_MESSENGER -> {
                iconType.setImageResource(R.drawable.ic_messenger)
                textType.text = itemView.context.getString(R.string.messenger)
                textType.setTextColor(ContextCompat.getColor(itemView.context,R.color.colorBlue_01))
            }
            TYPE_WHATSAPP -> {
                iconType.setImageResource(R.drawable.ic_whatsapp)
                textType.text = itemView.context.getString(R.string.whatsapp)
                textType.setTextColor(ContextCompat.getColor(itemView.context,R.color.colorGreen_01))
            }
            TYPE_INSTAGRAM -> {
                iconType.setImageResource(R.drawable.ic_instagram)
                textType.text = itemView.context.getString(R.string.instagram)
                textType.setTextColor(ContextCompat.getColor(itemView.context,R.color.colorRed_01))
            }
        }
    }

}