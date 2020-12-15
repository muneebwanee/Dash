package com.github.muneebwanee.dash.ui.animation

import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation

object AnimationUtils {

    fun View.animateAlpha() : AlphaAnimation = AlphaAnimation(0.0f,1.0f).apply {
            duration = 500
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
            startAnimation(this)
        }

}