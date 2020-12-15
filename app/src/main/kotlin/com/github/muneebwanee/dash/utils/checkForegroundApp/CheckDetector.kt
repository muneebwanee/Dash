package com.github.muneebwanee.dash.utils.checkForegroundApp

import android.content.Context

/**
 * Created by muneebwanee on 15/12/20.
 */
interface CheckDetector {

    fun getForegroundPostLollipop(context: Context): String?
    fun getForegroundPreLollipop(context: Context): String?

}