package com.github.muneebwanee.dash.data.rxFirebase

import android.net.Uri
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import java.io.File

/**
 * Created by muneebwanee on 15/12/20.
 */
interface InterfaceFirebase {

    fun getUser(): FirebaseUser?

    fun signIn(email: String, password: String): Maybe<AuthResult>

    fun signUp(email: String, password: String): Maybe<AuthResult>

    fun signOut()

    fun valueEvent(child: String): Flowable<DataSnapshot>

    fun valueEventAccount(): Flowable<DataSnapshot>

    fun queryValueEventSingle(child: String,value:String,id:String): Maybe<DataSnapshot>

    fun <T> valueEventModel(child: String, clazz: Class<T>): Flowable<T>

    fun getDatabaseReference(child: String): DatabaseReference

    fun getDatabaseAcount(): DatabaseReference

    fun getStorageReference(child: String): StorageReference

    fun getFile(child: String, file: File,progress:((progress:Int)->Unit)?=null): Single<FileDownloadTask.TaskSnapshot>

    fun putFile(child: String, uri: Uri,progress:((progress:Int)->Unit)?=null): Single<UploadTask.TaskSnapshot>

}