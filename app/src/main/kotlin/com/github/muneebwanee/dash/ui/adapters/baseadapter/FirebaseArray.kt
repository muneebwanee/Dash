package com.github.muneebwanee.dash.ui.adapters.basedapter


import android.annotation.SuppressLint
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.database.ObservableSnapshotArray
import com.firebase.ui.database.SnapshotParser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

import java.util.ArrayList


class FirebaseArray<T>(private val mQuery: Query, parser: SnapshotParser<T>,private val filter: String?,vararg val child: String?) : ObservableSnapshotArray<T>(parser), ChildEventListener, ValueEventListener {

    private val mSnapshots = ArrayList<DataSnapshot>()

    override fun onCreate() {
        super.onCreate()
        mQuery.addChildEventListener(this)
        mQuery.addValueEventListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mQuery.removeEventListener(this as ValueEventListener)
        mQuery.removeEventListener(this as ChildEventListener)
    }

    @SuppressLint("DefaultLocale")
    override fun onChildAdded(snapshot: DataSnapshot, previousChildKey: String?) {

        val index = getIndexForKey(snapshot.key!!)

        if (filter == null) {
            mSnapshots.add(index, snapshot)
            notifyOnChildChanged(ChangeEventType.ADDED, snapshot, index, -1)
        } else {

            val filtered1 = snapshot.child(child[0]!!).value!!.toString()

            if (child.size>1){
                val filtered2 = snapshot.child(child[1]!!).value!!.toString()
                if (filtered1.toLowerCase().contains(filter.toLowerCase()) ||
                        filtered2.toLowerCase().contains(filter.toLowerCase())) {
                    mSnapshots.add(index, snapshot)
                    notifyOnChildChanged(ChangeEventType.ADDED, snapshot, index, -1)
                }
            }else{
                if (filtered1.toLowerCase().contains(filter.toLowerCase())) {
                    mSnapshots.add(index, snapshot)
                    notifyOnChildChanged(ChangeEventType.ADDED, snapshot, index, -1)
                }
            }

        }

    }

    @SuppressLint("DefaultLocale")
    override fun onChildChanged(snapshot: DataSnapshot, previousChildKey: String?) {

        val index = getIndexForKey(snapshot.key!!)

        if (filter == null) {
            mSnapshots[index] = snapshot
            notifyOnChildChanged(ChangeEventType.CHANGED, snapshot, index, -1)
        } else {


            val filtered1 = snapshot.child(child[0]!!).value!!.toString()

            if (child.size>1){
                val filtered2 = snapshot.child(child[1]!!).value!!.toString()
                if (filtered1.toLowerCase().contains(filter.toLowerCase()) ||
                        filtered2.toLowerCase().contains(filter.toLowerCase())) {
                    mSnapshots[index] = snapshot
                    notifyOnChildChanged(ChangeEventType.CHANGED, snapshot, index, -1)
                }
            }else{
                if (filtered1.toLowerCase().contains(filter.toLowerCase())) {
                    mSnapshots[index] = snapshot
                    notifyOnChildChanged(ChangeEventType.CHANGED, snapshot, index, -1)
                }
            }

        }

    }

    override fun onChildRemoved(snapshot: DataSnapshot) {
        val index = getIndexKey(snapshot.key!!)

        mSnapshots.removeAt(index)
        notifyOnChildChanged(ChangeEventType.REMOVED, snapshot, index, -1)
    }

    override fun onChildMoved(snapshot: DataSnapshot, previousChildKey: String?) {
        val oldIndex = getIndexKey(snapshot.key!!)
        mSnapshots.removeAt(oldIndex)

        val newIndex = if (previousChildKey == null) 0 else getIndexKey(previousChildKey) + 1
        mSnapshots.add(newIndex, snapshot)

        notifyOnChildChanged(ChangeEventType.MOVED, snapshot, newIndex, oldIndex)
    }

    override fun onDataChange(snapshot: DataSnapshot) {
        notifyOnDataChanged()
    }

    override fun onCancelled(error: DatabaseError) {
        notifyOnError(error)
    }

    private fun getIndexForKey(key: String): Int {
        var index = 0
        for (snapshot in mSnapshots) {
            if (snapshot.key == key) {
                return index
            } else {
                index++
            }
        }
        return index
    }

    private fun getIndexKey(key: String): Int {
        var index = 0
        for (snapshot in mSnapshots) {
            if (snapshot.key == key) {
                return index
            } else {
                index++
            }
        }
        throw IllegalArgumentException("Key not found")
    }

    override fun getSnapshots(): List<DataSnapshot> {
        return mSnapshots
    }
}
