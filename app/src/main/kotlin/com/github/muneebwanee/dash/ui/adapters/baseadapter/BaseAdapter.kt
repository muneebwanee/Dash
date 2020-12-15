package com.github.muneebwanee.dash.ui.adapters.basedapter

import androidx.annotation.NonNull
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.database.ObservableSnapshotArray
import com.github.muneebwanee.dash.utils.Consts
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.pawegio.kandroid.e

abstract class BaseAdapter<T,VH : RecyclerView.ViewHolder>(@NonNull private var options : FirebaseOptions<T>) : RecyclerView.Adapter<VH>() , BaseInterfaceAdapter<T> {

    private var lastPosition = -1

    private var mSnapshots : ObservableSnapshotArray<T> = options.snapshots

    init {
        if (options.owner != null) options.owner!!.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    override fun startListening() {
        if (!mSnapshots.isListening(this))
            mSnapshots.addChangeEventListener(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    override fun stopListening() {
        mSnapshots.removeChangeEventListener(this)
        notifyDataSetChanged()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun cleanup(source : LifecycleOwner){
        source.lifecycle.removeObserver(this)
    }

    override fun onChildChanged(type: ChangeEventType, snapshot: DataSnapshot, newIndex: Int, oldIndex: Int) {
        when (type){
            ChangeEventType.ADDED -> notifyItemInserted(newIndex)
            ChangeEventType.CHANGED -> notifyItemChanged(newIndex)
            ChangeEventType.REMOVED -> notifyItemRemoved(newIndex)
            ChangeEventType.MOVED -> notifyItemMoved(oldIndex,newIndex)
        }
    }

    override fun onDataChanged() {}
    override fun startFilter() {}

    override fun onError(e: DatabaseError) {
        e(Consts.TAG,e.toException().toString())
    }

    override fun getSnapshots(): ObservableSnapshotArray<T> = mSnapshots

    override fun getItem(position: Int): T = mSnapshots[position]

    override fun getRef(position: Int): DatabaseReference = mSnapshots.getSnapshot(position).ref

    override fun getItemCount(): Int = if (mSnapshots.isListening(this)) mSnapshots.size else 0

    fun updateOptions(options: FirebaseOptions<T>){
        val wasListening = mSnapshots.isListening(this)
        if (this.options.owner!=null) this.options.owner!!.lifecycle.removeObserver(this)
        stopListening()
        this.options = options
        this.mSnapshots = options.snapshots
        if (options.owner!=null) options.owner.lifecycle.addObserver(this)
        if (wasListening) startListening()
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        onBindViewHolder(holder,position,getItem(position))
        /*holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.context,
                if (position > lastPosition) R.anim.down_from_top else R.anim.up_from_bottom))
        lastPosition = position*/
    }

    override fun onViewDetachedFromWindow(holder: VH) {
        super.onViewDetachedFromWindow(holder)
        //holder.itemView.clearAnimation()
    }

    abstract fun onBindViewHolder(holder : VH, position: Int, model : T)

}