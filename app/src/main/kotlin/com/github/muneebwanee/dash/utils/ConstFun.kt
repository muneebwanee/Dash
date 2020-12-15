package com.github.muneebwanee.dash.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.provider.MediaStore
import android.provider.Settings
import android.util.TypedValue
import androidx.annotation.IdRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.appbar.AppBarLayout
import androidx.core.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.github.clans.fab.FloatingActionMenu
import com.github.muneebwanee.dash.BuildConfig
import com.github.muneebwanee.dash.R
import com.github.muneebwanee.dash.data.model.Child
import com.github.muneebwanee.dash.ui.activities.login.LoginActivity
import com.github.muneebwanee.dash.ui.activities.mainparent.MainParentActivity
import com.github.muneebwanee.dash.ui.adapters.basedapter.FirebaseOptions
import com.github.muneebwanee.dash.ui.fragments.maps.MapsFragment
import com.github.muneebwanee.dash.ui.widget.toolbar.CustomToolbar
import com.github.muneebwanee.dash.utils.Consts.APP_DISABLED
import com.github.muneebwanee.dash.utils.Consts.APP_ENABLED
import com.github.muneebwanee.dash.utils.Consts.COMMAND_ENABLE_GPS_PROVIDER
import com.github.muneebwanee.dash.utils.Consts.COMMAND_ENABLE_NETWORK_PROVIDER
import com.github.muneebwanee.dash.utils.async.AsyncTaskRootPermission
import com.github.muneebwanee.dash.utils.async.AsyncTaskRunCommand
import com.google.firebase.database.Query
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pawegio.kandroid.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by muneebwanee on 15/12/20.
 */
object ConstFun {

    fun getPackageCheckSocial(): String = BuildConfig.PACKAGE_CHECK_SOCIAL

    fun getRandomNumeric() : String = System.currentTimeMillis().toString()

    inline fun <reified V : View> View.find(@IdRes id: Int): V = findViewById(id)

    fun isAndroidM(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    private fun isAndroidO() : Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    fun Context.setVibrate(milliseconds: Long) {
        if (isAndroidO()) vibrator!!.vibrate(VibrationEffect.createOneShot(milliseconds, 10))
        else vibrator!!.vibrate(milliseconds)
    }

    fun View.viewAnimation(anim: Techniques, duration: Long) =
            YoYo.with(anim).duration(duration).pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT).playOn(this)!!

    inline fun <reified T : Any> Activity.startAnimateActivity(enterAnim:Int, exitAnim:Int) {
        startActivity<T>()
        finish()
        animateActivity(enterAnim,exitAnim)
    }

    inline fun <reified T : Any> Activity.startAnimateActivity() { startActivity<T>() ; animateActivity(R.anim.fade_in,R.anim.fade_out) }

    fun Activity.startMain(fragmentTag:String){
        Intent(this,MainParentActivity::class.java).also {
            it.putExtra("TAG",fragmentTag)
            it.start(this)
            finish()
            animateActivity(R.anim.fade_in,R.anim.fade_out)
        }
    }

    inline fun <reified S : Any> Context.startServiceSms(smsAddress:String,smsBody:String,type:Int){
        val myIntent = IntentFor<S>(this)
        myIntent.putExtra(Consts.SMS_ADDRESS,smsAddress)
        myIntent.putExtra(Consts.SMS_BODY,smsBody)
        myIntent.putExtra(Consts.TYPE_SMS,type)
        startService(myIntent)
    }

    fun Activity.animateActivity(enterAnim:Int, exitAnim:Int) = overridePendingTransition(enterAnim, exitAnim)

    fun Activity.openGallery() = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also {
            it.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            it.type = "image/*"
            startActivityForResult(it,100)
        }

    fun View.isShow(state: Boolean) = if (state) show() else hide()

    fun Context.openAppSystemSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        intent.start(this)
    }

    fun runThread(sleep: Long, action: () -> Unit): Thread = Thread(Runnable {
        while (true) {
            try {
                Thread.sleep(sleep)
                runOnUiThread { action() }
            } catch (e: InterruptedException) {
                break
            }
        }
    })

    @SuppressLint("SimpleDateFormat")
    fun getDateTime(): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy hh:mm a")
        return dateFormat.format(Calendar.getInstance().time)
    }

    fun Context.alertDialog(alertType: Int, title: Int, msg: String?, txtPositiveButton: Int?, cancellable: Boolean = false, action: SweetAlertDialog.() -> Unit): SweetAlertDialog {
        val dialog = SweetAlertDialog(this, alertType).apply {
            titleText = getString(title)
            setCancelable(cancellable)

            if (alertType != SweetAlertDialog.PROGRESS_TYPE) {
                contentText = msg
                confirmText = getString(txtPositiveButton!!)
                if (cancellable) cancelText = getString(android.R.string.cancel)
                showCancelButton(cancellable)
            } else progressHelper.barColor = ContextCompat.getColor(this@alertDialog, R.color.colorPrimary)
        }
        action(dialog)
        return dialog
    }

    fun Context.alertDialog(v:View,cancellable: Boolean = false) : AlertDialog =
            AlertDialog.Builder(this).setView(v)
                    .setCancelable(cancellable).create()

    fun ImageView.setImageUrl(url: String,placeholder: Int) {
        Glide.with(this).load(url)
                .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
                .apply(RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        //.error(context.getDrawable(R.drawable.ic_placeholder_error))
                        .placeholder(context.getDrawable(placeholder))
                )
                .into(this)
    }

    fun ImageView.setImageId(drawable:Int){
        Glide.with(this).load(drawable)
                .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(this)
    }

    fun convertCurrentDuration(currentDuration: Long): String {
        val seconds = (currentDuration % 60000 / 1000).toString()
        val minutes = (currentDuration / 60000).toString()
        return if (seconds.length == 1) "$minutes:0$seconds" else "$minutes:$seconds"
    }

    //FirebaseOptions
    inline fun <reified T> firebaseOptions(query: Query,filter:String?=null,vararg child:String?): FirebaseOptions<T> =
            FirebaseOptions.Builder<T>().setQuery(query, T::class.java,filter,*child).build()

    fun customAnimationMenu(menu: FloatingActionMenu, drawableOpen: Int, drawableClose: Int) {
        val set = AnimatorSet()
        val scaleOutX = ObjectAnimator.ofFloat(menu.menuIconView, "scaleX", 1.0f, 0.2f)
        val scaleOutY = ObjectAnimator.ofFloat(menu.menuIconView, "scaleY", 1.0f, 0.2f)
        val scaleInX = ObjectAnimator.ofFloat(menu.menuIconView, "scaleX", 0.2f, 1.0f)
        val scaleInY = ObjectAnimator.ofFloat(menu.menuIconView, "scaleY", 0.2f, 1.0f)
        scaleOutX.duration = 50
        scaleOutY.duration = 50
        scaleInX.duration = 150
        scaleInY.duration = 150
        scaleInX.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                menu.menuIconView.setImageResource(if (menu.isOpened) drawableOpen else drawableClose)
            }
        })
        set.play(scaleOutX).with(scaleOutY)
        set.play(scaleInX).with(scaleInY).after(scaleOutX)
        //set.interpolator = OvershootInterpolator(2f)
        menu.iconToggleAnimatorSet = set
    }

    fun Context.openAccessibilitySettings() = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).also { it.start(this) }

    fun Context.openUseAccessSettings() = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).also { it.start(this) }

    fun Context.openNotificationListenerSettings(){
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
            Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        else Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
        intent.start(this)
    }

    @SuppressLint("BatteryLife")
    fun Context.openWhitelistSettings(){
        if (isAndroidM()) Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).also {
            it.data = "package:$packageName".toUri()
            it.start(this)
        }
    }

    fun isRoot() : Boolean {
        if (Build.TAGS != null && Build.TAGS.contains("test-keys")) return true
        val patch = arrayOf("/su/bin","/sbin", "/system/bin", "/system/sbin", "/system/xbin", "/data/local/xbin",
                "/data/local/bin", "/system/sd/xbin", "/system/bin/failsafe", "/vendor/bin", "/data/local")
        var result = false
        for (element in patch) if (File("$element/su").exists()){ result = true ; break }
        return result
    }

    fun Context.permissionRoot(result:(result:Boolean)->Unit) =
            AsyncTaskRootPermission(this){ result(it) }.execute()!!

    fun enableGpsRoot() = AsyncTaskRunCommand(onPostFunc = { enableNetworkProviderRoot() }).execute(COMMAND_ENABLE_GPS_PROVIDER)!!
    private fun enableNetworkProviderRoot() = AsyncTaskRunCommand().execute(COMMAND_ENABLE_NETWORK_PROVIDER)!!

    @SuppressLint("WrongConstant")
    fun Context.showApp(state:Boolean){
        val componentName = ComponentName(this, LoginActivity::class.java)
        packageManager.setComponentEnabledSetting(componentName,
                if (state) APP_ENABLED else APP_DISABLED,
                PackageManager.DONT_KILL_APP)
    }

    fun Context.isNotificationServiceRunning() : Boolean {
        val enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        return enabledNotificationListeners != null && enabledNotificationListeners.contains(packageName)
    }

    fun Context.isAddWhitelist() : Boolean =
            if (isAndroidM()) powerManager!!.isIgnoringBatteryOptimizations(packageName)
            else true


    fun View.showKeyboard(show:Boolean){
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (show) imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
        else imm.hideSoftInputFromWindow(this.windowToken, 0)
    }

    fun contentGlobalLayout(content: ConstraintLayout, appBar: AppBarLayout){
        content.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                content.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val appbar = appBar.height
                content.translationY = -appbar.toFloat()
                content.layoutParams.height = content.height + appbar
            }
        })
    }

    fun Context.adjustFontScale() {
        val configuration = resources.configuration
        if (configuration.fontScale > 1) {
            configuration.fontScale = 0.85f
            val metrics = resources.displayMetrics
            val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            wm.defaultDisplay.getMetrics(metrics)
            metrics.scaledDensity = configuration.fontScale * metrics.density
            createConfigurationContext(configuration)
            //resources.updateConfiguration(configuration, metrics)
        }
    }

    fun Context.sendToGoogleMaps(latitude:String,longitude:String){
        try {
            val uri = "geo:0,0?q=$latitude,$longitude".toUri()
            Intent(Intent.ACTION_VIEW,uri).also {
                it.setPackage("com.google.android.apps.maps")
                if (it.resolveActivity(packageManager)!=null) startActivity(it)
                else longToast(R.string.maps_installed)
            }
        }catch (t:Throwable){
            longToast(R.string.maps_installed)
        }
    }

    fun isScrollToolbar(toolbar:CustomToolbar,state:Boolean){
        val params = toolbar.layoutParams as AppBarLayout.LayoutParams
        if (state) params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
        else params.scrollFlags = 0
    }

    fun listToStringChild(list: MutableList<Child>): String = Gson().toJson(list, object : TypeToken<MutableList<Child>>(){}.type)

    fun stringToListChild(string: String): MutableList<Child> = Gson().fromJson(string, object : TypeToken<MutableList<Child>>(){}.type)

    fun View?.setPaddingCompass(top:AppBarLayout){
        try {
            if (this!=null){
                val parent = findViewWithTag<View>("GoogleMapMyLocationButton").parent as ViewGroup
                parent.post {
                    val marginPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics).toInt()
                    val mapCompass = parent.getChildAt(4)
                    val rlp = RelativeLayout.LayoutParams(mapCompass.height, mapCompass.height)
                    rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                    rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                    rlp.setMargins(marginPixels,top.height,marginPixels,marginPixels)
                    mapCompass.layoutParams = rlp
                }
            }
        } catch (t: Throwable) {
            e(MapsFragment.TAG,t.message.toString())
        }
    }
}