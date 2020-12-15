package com.github.muneebwanee.dash.ui.adapters.basedapter


import androidx.lifecycle.LifecycleOwner

import com.firebase.ui.database.ClassSnapshotParser
import com.firebase.ui.database.ObservableSnapshotArray
import com.google.firebase.database.Query


class FirebaseOptions<T> private constructor(val snapshots: ObservableSnapshotArray<T>, val owner: LifecycleOwner?) {

    class Builder<T> {

        private var mSnapshots: ObservableSnapshotArray<T>? = null
        private var mOwner: LifecycleOwner? = null

        fun setQuery(query: Query, modelClass: Class<T>,filter: String?, vararg child: String?): Builder<T> {
            mSnapshots = FirebaseArray(query, ClassSnapshotParser(modelClass),filter,*child)
            return this
        }

        fun setLifecycleOwner(owner: LifecycleOwner?): Builder<T> {
            mOwner = owner
            return this
        }

        fun build(): FirebaseOptions<T> {
            return FirebaseOptions(mSnapshots!!, mOwner)
        }
    }

}
