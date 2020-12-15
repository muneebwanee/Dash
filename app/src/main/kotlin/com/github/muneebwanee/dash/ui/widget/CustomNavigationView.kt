package com.github.muneebwanee.dash.ui.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import com.github.muneebwanee.dash.R
import com.google.android.material.navigation.NavigationView

/**
 * Created by muneebwanee on 15/12/20.
 */
class CustomNavigationView @JvmOverloads constructor(context: Context,
                           attributeSet: AttributeSet,
                           defStyleAttr:Int = R.style.Widget_NavigationView
) : NavigationView(context,attributeSet,defStyleAttr) {

    init {
        itemBackground = navigationItemBackground()
    }

    private fun navigationItemBackground() : Drawable?{
        var background = AppCompatResources.getDrawable(context,R.drawable.navigation_item_background)
        if (background!=null){
            val tint = AppCompatResources.getColorStateList(context,R.color.navigation_item_background_tint)
            background = DrawableCompat.wrap(background.mutate())
            background.setTintList(tint)
        }
        return background
    }

}