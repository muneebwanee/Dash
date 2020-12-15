package com.github.muneebwanee.dash.utils.checkForegroundApp

import android.annotation.TargetApi
import android.app.ActivityManager
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.github.muneebwanee.dash.utils.checkForegroundApp.CheckPermission.hasUsageStatsPermission

/**
 * Created by muneebwanee on 15/12/20.
 */
class CheckForegroundApp : CheckDetector {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun getForegroundPostLollipop(context: Context): String? {

        if (!context.hasUsageStatsPermission()) return null

        var foregroundApp: String? = null

        val mUsageStatsManager = context.getSystemService(Service.USAGE_STATS_SERVICE) as UsageStatsManager
        val time = System.currentTimeMillis()

        val usageEvents = mUsageStatsManager.queryEvents(time - 1000 * 3600, time)
        val event = UsageEvents.Event()
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                foregroundApp = event.packageName
            }
        }

        return foregroundApp
    }

    override fun getForegroundPreLollipop(context: Context): String? {
        val am = context.getSystemService(Service.ACTIVITY_SERVICE) as ActivityManager
        val foregroundTaskInfo = am.getRunningTasks(1)[0]
        val foregroundTaskPackageName = foregroundTaskInfo?.topActivity?.packageName
        val pm = context.packageManager
        val foregroundAppPackageInfo: PackageInfo?
        try {
            foregroundAppPackageInfo = pm.getPackageInfo(foregroundTaskPackageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            return null
        }

        return foregroundAppPackageInfo?.applicationInfo?.packageName
    }
}