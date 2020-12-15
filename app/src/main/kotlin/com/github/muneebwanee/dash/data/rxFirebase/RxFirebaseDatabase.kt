package com.github.muneebwanee.dash.data.rxFirebase

import com.github.muneebwanee.dash.utils.Consts.TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.pawegio.kandroid.e
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.exceptions.OnErrorNotImplementedException
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import io.reactivex.Maybe

/**
 * Created by muneebwanee on 15/12/20.
 */
object RxFirebaseDatabase {

    fun Query.rxObserveValueEvent(auth : FirebaseAuth): Flowable<DataSnapshot> {
        return Flowable.create({ emitter ->
            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) = emitter.onNext(dataSnapshot)

                override fun onCancelled(error: DatabaseError) {
                    try { if (auth.currentUser!=null) if (!emitter.isCancelled) emitter.onError(Throwable(error.message)) }
                    catch (er: OnErrorNotImplementedException){ e(TAG, er.message.toString()) }
                    catch (t: Throwable) { e(TAG, t.message.toString()) }
                }
            }
            emitter.setCancellable { removeEventListener(valueEventListener) }
            addValueEventListener(valueEventListener)
        }, BackpressureStrategy.DROP)
    }

    fun Query.rxObserveSingleValueEvent(): Maybe<DataSnapshot> {
        return Maybe.create { emitter ->
            addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) = emitter.onSuccess(dataSnapshot)
                override fun onCancelled(error: DatabaseError) { if (!emitter.isDisposed) emitter.onError(Throwable(error.message)) }
            })
        }
    }

}