package com.github.muneebwanee.dash.data.rxFirebase

import android.net.Uri
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import io.reactivex.Single
import java.io.File

/**
 * Created by muneebwanee on 15/12/20.
 */
object RxFirebaseStorage {

    fun StorageReference.rxGetFile(destinationFile: File,progress:((progress:Int)->Unit)?=null): Single<FileDownloadTask.TaskSnapshot> {
        return Single.create { emitter ->
            val taskSnapshotStorageTask = getFile(destinationFile)
                    .addOnSuccessListener { taskSnapshot -> emitter.onSuccess(taskSnapshot) }
                    .addOnFailureListener { error -> if (!emitter.isDisposed) { emitter.onError(error) } }
                    .addOnProgressListener { if (progress!=null) progress(((100.0 * it.bytesTransferred) / it.totalByteCount).toInt()) }
            emitter.setCancellable { taskSnapshotStorageTask.cancel() }
        }
    }

    fun StorageReference.rxPutFile(uri: Uri,progress:((progress:Int)->Unit)?=null): Single<UploadTask.TaskSnapshot> {
        return Single.create<UploadTask.TaskSnapshot> { emitter ->
            val taskSnapshotStorageTask = putFile(uri)
                    .addOnSuccessListener { taskSnapshot -> emitter.onSuccess(taskSnapshot) }
                    .addOnFailureListener { error -> if (!emitter.isDisposed) emitter.onError(error) }
                    .addOnProgressListener { if (progress!=null) progress(((100.0 * it.bytesTransferred) / it.totalByteCount).toInt()) }
            emitter.setCancellable { taskSnapshotStorageTask.cancel() }
        }
    }

}