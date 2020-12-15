package com.github.muneebwanee.dash.data.rxFirebase

import com.github.muneebwanee.dash.utils.Consts.TAG
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.pawegio.kandroid.e
import io.reactivex.MaybeEmitter

/**
 * Created by muneebwanee on 15/12/20.
 */
class RxTask<T>(private val emitter: MaybeEmitter<in T>) : OnSuccessListener<T>, OnFailureListener, OnCompleteListener<T> {

    override fun onSuccess(res: T?) {
        if (res != null) emitter.onSuccess(res)
        else emitter.onError(Throwable("Observables can't emit null values"))

    }

    override fun onComplete(task: Task<T>) {
        emitter.onComplete()
    }

    override fun onFailure(e: Exception) {
        if (!emitter.isDisposed) {
            emitter.onError(e)
        }

    }

    companion object {

        fun <T> assignOnTask(emitter: MaybeEmitter<in T>, task: Task<T>) {
            val rxTask = RxTask(emitter)
            task.addOnSuccessListener(rxTask)
            task.addOnFailureListener(rxTask)

            try {
                task.addOnCompleteListener(rxTask)
            } catch (t: Throwable) {
                e(TAG, t.message.toString())
            }

        }
    }
}
