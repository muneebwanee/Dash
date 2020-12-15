package com.github.muneebwanee.dash.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import cn.pedant.SweetAlert.SweetAlertDialog
import com.github.muneebwanee.dash.R
import com.github.muneebwanee.dash.data.model.Child
import com.github.muneebwanee.dash.data.preference.DataSharePreference.childPhoto
import com.github.muneebwanee.dash.data.preference.DataSharePreference.childSelected
import com.github.muneebwanee.dash.data.preference.DataSharePreference.deviceChildSelected
import com.github.muneebwanee.dash.utils.ConstFun.alertDialog
import com.github.muneebwanee.dash.utils.ConstFun.find
import com.github.muneebwanee.dash.utils.ConstFun.isShow
import com.github.muneebwanee.dash.utils.ConstFun.setImageUrl
import com.pawegio.kandroid.inflateLayout
import de.hdodenhof.circleimageview.CircleImageView

object Dialogs {

    fun Activity.showDialogAccount(list: MutableList<Child>,cancellable: Boolean = true,clickChangePhoto:(() -> Unit)?=null,selected:(dialog:AlertDialog?)->Unit) : AlertDialog{

        var dialog : AlertDialog?=null

        val v = inflateLayout(R.layout.dialog_select_child,null,false)
        val linearContent = v.find<LinearLayout>(R.id.content_child)
        val linearSelected = v.find<LinearLayout>(R.id.linear_selected_child)
        val photoSelected = v.find<CircleImageView>(R.id.photo_selected_child)
        val nameSelected = v.find<TextView>(R.id.name_selected_child)
        val deviceSelected = v.find<TextView>(R.id.device_selected_child)
        val changePhoto = v.find<LinearLayout>(R.id.change_photo_selected_child)

        linearSelected.isShow(childSelected!="")
        nameSelected.text = childSelected
        deviceSelected.text = deviceChildSelected
        if (childPhoto.isNotEmpty()) photoSelected.setImageUrl(childPhoto,R.drawable.ic_placeholder_profile)

        changePhoto.setOnClickListener { if (clickChangePhoto!=null) clickChangePhoto() }

        linearContent.removeAllViews()
        for (i in 0 until list.size){
            val child = list[i]
            if (child.name != childSelected){
                val vItem = inflateLayout(R.layout.item_select_child)
                val photoChild = vItem.find<CircleImageView>(R.id.item_photo_selected_account)
                val nameChild = vItem.find<TextView>(R.id.item_name_selected_account)
                val deviceChild = vItem.find<TextView>(R.id.item_device_selected_account)
                val click = vItem.find<LinearLayout>(R.id.item_linear_selected_account)
                if (!child.photoUrl.isNullOrEmpty()) photoChild.setImageUrl(child.photoUrl,R.drawable.ic_placeholder_profile)
                nameChild.text = child.name
                deviceChild.text = child.nameDevice
                click.setOnClickListener {
                    if (!child.photoUrl.isNullOrEmpty()) childPhoto = child.photoUrl
                    childSelected = child.name
                    deviceChildSelected = child.nameDevice
                    selected(dialog)
                }
                linearContent.addView(vItem)
            }
        }

        dialog = alertDialog(v,cancellable)
        dialog.show()
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    fun Context.showViewGetRecordAudio(action:(timer:Long)->Unit){

        val values = arrayOf(getString(R.string.one_minute),getString(R.string.two_minute),getString(R.string.five_minute),getString(R.string.ten_minute),getString(R.string.fifteen_minute),getString(R.string.thirty_minute),getString(R.string.one_hour))
        var timer:Long = 60000

        val view = inflateLayout(R.layout.view_get_recording)
        val picker = view.find<NumberPicker>(R.id.view_picker_record)
        picker.minValue = 0
        picker.maxValue = values.size -1
        picker.displayedValues = values
        picker.wrapSelectorWheel = true

        SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE).apply {
            setCustomView(view)
            titleText = getString(R.string.message_dialog_record_audio)
            confirmText = getString(R.string.submit)
            cancelText = getString(android.R.string.cancel)
            showCancelButton(true)
            setCancelable(true)
            setCancelClickListener { dismissWithAnimation() }
            setConfirmClickListener {
                dismissWithAnimation()
                action(timer)
            }
            show()
        }

        picker.setOnValueChangedListener { _, _, newVal ->
            when (newVal){
                0 -> timer = 60000
                1 -> timer = 120000
                2 -> timer = 300000
                3 -> timer = 600000
                4 -> timer = 900000
                5 -> timer = 1800000
                6 -> timer = 3600000
            }
        }
    }

}