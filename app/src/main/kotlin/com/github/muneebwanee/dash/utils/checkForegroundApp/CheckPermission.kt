package com.github.muneebwanee.dash.utils.checkForegroundApp

import android.app.AppOpsManager
import android.content.Context
import com.github.muneebwanee.dash.utils.Consts.PERMISSION_USAGE_STATS
import com.pawegio.kandroid.isPermissionGranted

/**
 * Created by muneebwanee on 15/12/20.
 */
object CheckPermission{

    fun Context.hasUsageStatsPermission(): Boolean {
        val mode = getModeManager(AppOpsManager.OPSTR_GET_USAGE_STATS)
        return if (mode == AppOpsManager.MODE_DEFAULT) isPermissionGranted(PERMISSION_USAGE_STATS)
        else mode == AppOpsManager.MODE_ALLOWED
    }

    fun Context.getModeManager(ops:String) : Int{
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        return appOps.checkOpNoThrow(ops, android.os.Process.myUid(), packageName)
    }
}