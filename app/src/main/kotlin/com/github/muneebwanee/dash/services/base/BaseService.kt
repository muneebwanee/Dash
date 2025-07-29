package com.github.muneebwanee.dash.services.base  
  
import android.app.Service  
import android.content.Intent  
import android.os.IBinder  
import androidx.lifecycle.LifecycleService  
import dagger.hilt.android.AndroidEntryPoint  
import kotlinx.coroutines.CoroutineScope  
import kotlinx.coroutines.Dispatchers  
import kotlinx.coroutines.SupervisorJob  
import kotlinx.coroutines.cancel  
import timber.log.Timber  
import javax.inject.Inject  
  
@AndroidEntryPoint  
abstract class BaseService : LifecycleService() {  
  
    protected val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)  
  
    @Inject  
    lateinit var serviceInteractor: BaseServiceInteractor  
  
    override fun onCreate() {  
        super.onCreate()  
        Timber.d("${this::class.simpleName} created")  
    }  
  
    override fun onBind(intent: Intent): IBinder? = null  
  
    override fun onDestroy() {  
        super.onDestroy()  
        serviceScope.cancel()  
        Timber.d("${this::class.simpleName} destroyed")  
    }  
  
    protected fun startForegroundServiceWithNotification(  
        notificationId: Int,  
        channelId: String,  
        title: String,  
        content: String  
    ) {  
        val notification = serviceInteractor.createServiceNotification(  
            channelId, title, content  
        )  
        startForeground(notificationId, notification)  
    }  
}
