package com.github.muneebwanee.dash.ui.activities.mainparent

import com.github.muneebwanee.dash.ui.activities.base.InterfaceView
import com.google.firebase.database.DataSnapshot

/**
 * Created by muneebwanee on 15/12/20.
 */
interface InterfaceViewMainParent : InterfaceView {
    fun signOutView()
    fun onFinishCount()
    fun setCheckedNavigationItem(id: Int)
    fun closeNavigationDrawer()
    fun requestApplyInsets()
    fun setDataAccounts(data: DataSnapshot)
}