package com.github.muneebwanee.dash.app  
  
import android.app.Application  
import android.app.NotificationChannel  
import android.app.NotificationManager  
import android.os.Build  
import androidx.hilt.work.HiltWorkerFactory  
import androidx.work.Configuration  
import androidx.work.WorkManager  
import com.google.firebase.FirebaseApp  
import com.google.firebase.database.FirebaseDatabase  
import com.google.firebase.storage.FirebaseStorage  
import dagger.hilt.android.HiltAndroidApp  
import timber.log.Timber  
import javax.inject.Inject  
  
@HiltAndroidApp  
class DashApplication : Application(), Configuration.Provider {  
  
    @Inject  
    lateinit var workerFactory: HiltWorkerFactory  
  
    companion object {  
        const val MONITORING_CHANNEL_ID = "monitoring_channel"  
        const val DATA_SYNC_CHANNEL_ID = "data_sync_channel"  
        const val LOCATION_CHANNEL_ID = "location_channel"  
          
        lateinit var instance: DashApplication  
            private set  
    }  
  
    override fun onCreate() {  
        super.onCreate()  
        instance = this  
          
        // Initialize logging  
        if (BuildConfig.DEBUG) {  
            Timber.plant(Timber.DebugTree())  
        }  
          
        // Initialize Firebase  
        initializeFirebase()  
          
        // Create notification channels  
        createNotificationChannels()  
          
        // Initialize WorkManager  
        initializeWorkManager()  
          
        Timber.d("DashApplication initialized")  
    }  
  
    private fun initializeFirebase() {  
        try {  
            FirebaseApp.initializeApp(this)  
              
            // Enable offline persistence for Firestore  
            val database = FirebaseDatabase.getInstance()  
            database.setPersistenceEnabled(true)  
            database.setPersistenceCacheSizeBytes(50 * 1024 * 1024) // 50MB cache  
              
            Timber.d("Firebase initialized successfully")  
        } catch (e: Exception) {  
            Timber.e(e, "Failed to initialize Firebase")  
        }  
    }  
  
    private fun createNotificationChannels() {  
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {  
            val notificationManager = getSystemService(NotificationManager::class.java)  
              
            // Monitoring service channel  
            val monitoringChannel = NotificationChannel(  
                MONITORING_CHANNEL_ID,  
                "Monitoring Services",  
                NotificationManager.IMPORTANCE_LOW  
            ).apply {  
                description = "Notifications for active monitoring services"  
                setShowBadge(false)  
            }  
              
            // Data sync channel  
            val dataSyncChannel = NotificationChannel(  
                DATA_SYNC_CHANNEL_ID,  
                "Data Synchronization",  
                NotificationManager.IMPORTANCE_MIN  
            ).apply {  
                description = "Background data synchronization notifications"  
                setShowBadge(false)  
            }  
              
            // Location tracking channel  
            val locationChannel = NotificationChannel(  
                LOCATION_CHANNEL_ID,  
                "Location Tracking",  
                NotificationManager.IMPORTANCE_LOW  
            ).apply {  
                description = "Location tracking service notifications"  
                setShowBadge(false)  
            }  
              
            notificationManager.createNotificationChannels(  
                listOf(monitoringChannel, dataSyncChannel, locationChannel)  
            )  
              
            Timber.d("Notification channels created")  
        }  
    }  
  
    private fun initializeWorkManager() {  
        try {  
            WorkManager.initialize(this, workManagerConfiguration)  
            Timber.d("WorkManager initialized")  
        } catch (e: Exception) {  
            Timber.e(e, "Failed to initialize WorkManager")  
        }  
    }  
  
    override fun getWorkManagerConfiguration(): Configuration {  
        return Configuration.Builder()  
            .setWorkerFactory(workerFactory)  
            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) android.util.Log.DEBUG else android.util.Log.ERROR)  
            .build()  
    }  
  
    override fun onTerminate() {  
        super.onTerminate()  
        Timber.d("DashApplication terminated")  
    }  
  
    override fun onLowMemory() {  
        super.onLowMemory()  
        Timber.w("Low memory warning")  
    }  
  
    override fun onTrimMemory(level: Int) {  
        super.onTrimMemory(level)  
        Timber.w("Memory trim requested: level $level")  
    }  
}
