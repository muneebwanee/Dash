package com.github.muneebwanee.dash.data.preference

import android.content.Context
import androidx.core.content.edit

/**
 * Created by muneebwanee on 15/12/20.
 */
object DataSharePreference{

    var Context.statePersmissionPhotoShow : Boolean
        get() = getSharedPreferences("statePersmissionPhotoShow",Context.MODE_PRIVATE).getBoolean("state",false)
        set(state) = getSharedPreferences("statePersmissionPhotoShow",Context.MODE_PRIVATE).edit { putBoolean("state",state) }

    var Context.statePersmissionLocationShow : Boolean
        get() = getSharedPreferences("statePersmissionLocationShow",Context.MODE_PRIVATE).getBoolean("state",false)
        set(state) = getSharedPreferences("statePersmissionLocationShow",Context.MODE_PRIVATE).edit { putBoolean("state",state) }

    var Context.lockPin:String
        get() = getSharedPreferences("lock",Context.MODE_PRIVATE).getString("pin","")!!
        set(pin) = getSharedPreferences("lock",Context.MODE_PRIVATE).edit{ putString("pin",pin) }

    var Context.lockState:Boolean
        get() = getSharedPreferences("lockState",Context.MODE_PRIVATE).getBoolean("state",false)
        set(state) = getSharedPreferences("lockState",Context.MODE_PRIVATE).edit { putBoolean("state",state) }

    var Context.finishAppState:Boolean
        get() = getSharedPreferences("finishAppState",Context.MODE_PRIVATE).getBoolean("state",false)
        set(state) = getSharedPreferences("finishAppState",Context.MODE_PRIVATE).edit { putBoolean("state",state) }

    var Context.timeFinishApp : Int
        get() = getSharedPreferences("timeFinishApp",Context.MODE_PRIVATE).getInt("time",1000)
        set(time) = getSharedPreferences("timeFinishApp",Context.MODE_PRIVATE).edit { putInt("time",time) }

    var Context.typeApp : Boolean
        get() = getSharedPreferences("typeApp",Context.MODE_PRIVATE).getBoolean("type",false)
        set(type) =  getSharedPreferences("typeApp",Context.MODE_PRIVATE).edit { putBoolean("type",type) }

    fun Context.setSelectedItem(id:String,selected:Boolean) =
            getSharedPreferences("selectedItem",Context.MODE_PRIVATE).edit { putBoolean(id,selected) }

    fun Context.getSelectedItem(id:String) : Boolean =
            getSharedPreferences("selectedItem",Context.MODE_PRIVATE).getBoolean(id,false)

    fun Context.clearSelectedItem() = getSharedPreferences("selectedItem",Context.MODE_PRIVATE).edit().clear().commit()

    var Context.childPhoto :String
        get() = getSharedPreferences("childPhoto",Context.MODE_PRIVATE).getString("icon","")!!
        set(url) = getSharedPreferences("childPhoto",Context.MODE_PRIVATE).edit { putString("icon",url) }

    var Context.childSelected : String
        get() = getSharedPreferences("child",Context.MODE_PRIVATE).getString("user","")!!
        set(user) = getSharedPreferences("child",Context.MODE_PRIVATE).edit { putString("user",user) }

    var Context.deviceChildSelected : String
        get() = getSharedPreferences("deviceChildSelected",Context.MODE_PRIVATE).getString("device","")!!
        set(device) = getSharedPreferences("deviceChildSelected",Context.MODE_PRIVATE).edit { putString("device",device) }

    var Context.listChild : String
        get() = getSharedPreferences("listChild",Context.MODE_PRIVATE).getString("list","[]")!!
        set(list) = getSharedPreferences("listChild",Context.MODE_PRIVATE).edit { putString("list",list) }

    fun Context.clearAll() {
        getSharedPreferences("statePersmissionPhotoShow",Context.MODE_PRIVATE).edit().clear().apply()
        getSharedPreferences("statePersmissionLocationShow",Context.MODE_PRIVATE).edit().clear().apply()
        getSharedPreferences("lock",Context.MODE_PRIVATE).edit().clear().apply()
        getSharedPreferences("lockState",Context.MODE_PRIVATE).edit().clear().apply()
        getSharedPreferences("finishAppState",Context.MODE_PRIVATE).edit().clear().apply()
        getSharedPreferences("timeFinishApp",Context.MODE_PRIVATE).edit().clear().apply()
        getSharedPreferences("childPhoto",Context.MODE_PRIVATE).edit().clear().apply()
        getSharedPreferences("child",Context.MODE_PRIVATE).edit().clear().apply()
        getSharedPreferences("listChild",Context.MODE_PRIVATE).edit().clear().apply()
        getSharedPreferences("typeApp",Context.MODE_PRIVATE).edit().clear().apply()
        clearSelectedItem()
    }

}