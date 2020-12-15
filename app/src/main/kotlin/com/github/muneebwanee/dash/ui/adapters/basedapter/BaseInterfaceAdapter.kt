package com.github.muneebwanee.dash.ui.adapters.basedapter

import androidx.annotation.NonNull
import androidx.lifecycle.LifecycleObserver
import com.firebase.ui.database.ChangeEventListener
import com.firebase.ui.database.ObservableSnapshotArray
import com.google.firebase.database.DatabaseReference

interface BaseInterfaceAdapter<T> : ChangeEventListener, LifecycleObserver {

    fun startListening()
    fun stopListening()
    fun startFilter()
    @NonNull fun getSnapshots() : ObservableSnapshotArray<T>
    @NonNull fun getItem(position : Int) : T
    @NonNull fun getRef(position: Int) : DatabaseReference

}