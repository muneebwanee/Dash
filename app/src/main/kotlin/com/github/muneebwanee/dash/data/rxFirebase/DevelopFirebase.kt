package com.github.muneebwanee.dash.data.rxFirebase

import android.content.Context
import android.net.Uri
import com.github.muneebwanee.dash.utils.Consts.USER
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.StorageReference
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import java.io.File
import javax.inject.Inject
import com.github.muneebwanee.dash.data.rxFirebase.RxFirebaseDatabase.rxObserveValueEvent
import com.github.muneebwanee.dash.data.rxFirebase.RxFirebaseDatabase.rxObserveSingleValueEvent
import com.github.muneebwanee.dash.data.rxFirebase.RxFirebaseAuth.rxSignInWithEmailAndPassword
import com.github.muneebwanee.dash.data.rxFirebase.RxFirebaseAuth.rxCreateUserWithEmailAndPassword
import com.github.muneebwanee.dash.data.rxFirebase.RxFirebaseStorage.rxGetFile
import com.github.muneebwanee.dash.data.rxFirebase.RxFirebaseStorage.rxPutFile
import com.github.muneebwanee.dash.data.preference.DataSharePreference.childSelected
import com.google.firebase.storage.UploadTask

/**
 * Created by muneebwanee on 15/12/20.
 */
class DevelopFirebase @Inject constructor(private val context: Context,
                                          private val auth: FirebaseAuth,
                                          private val dataRef: DatabaseReference,
                                          private val stoRef: StorageReference) : InterfaceFirebase {

    override fun getUser(): FirebaseUser? = auth.currentUser

    override fun signIn(email: String, password: String): Maybe<AuthResult> =
            auth.rxSignInWithEmailAndPassword(email, password)

    override fun signUp(email: String, password: String): Maybe<AuthResult> =
            auth.rxCreateUserWithEmailAndPassword(email, password)

    override fun signOut() {
        auth.signOut()
    }

    override fun getDatabaseReference(child: String): DatabaseReference =
            getDatabaseAcount().child(context.childSelected).child(child)

    override fun getDatabaseAcount(): DatabaseReference {
        val reference = dataRef.child(USER).child(getUser()!!.uid)
        reference.keepSynced(true)
        return reference
    }

    override fun getStorageReference(child: String): StorageReference =
            stoRef.child(USER).child(getUser()!!.uid).child(context.childSelected).child(child)

    override fun valueEventAccount(): Flowable<DataSnapshot> =
            getDatabaseAcount().rxObserveValueEvent(auth)

    override fun valueEvent(child: String): Flowable<DataSnapshot> =
            getDatabaseReference(child).rxObserveValueEvent(auth)

    override fun <T> valueEventModel(child: String, clazz: Class<T>): Flowable<T> =
            getDatabaseReference(child).rxObserveValueEvent(auth).map { it.getValue(clazz) }

    override fun getFile(child: String, file: File, progress: ((progress: Int) -> Unit)?): Single<FileDownloadTask.TaskSnapshot> =
            getStorageReference(child).rxGetFile(file,progress)

    override fun putFile(child: String, uri: Uri, progress: ((progress: Int) -> Unit)?): Single<UploadTask.TaskSnapshot> =
            getStorageReference(child).rxPutFile(uri,progress)

    override fun queryValueEventSingle(child: String, value: String, id: String): Maybe<DataSnapshot> =
            getDatabaseReference(child).orderByChild(value).equalTo(id).rxObserveSingleValueEvent()

}