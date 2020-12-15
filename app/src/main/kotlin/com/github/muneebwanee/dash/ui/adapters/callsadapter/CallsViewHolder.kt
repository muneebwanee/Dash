package com.github.muneebwanee.dash.ui.adapters.callsadapter

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.*
import at.grabner.circleprogress.CircleProgressView
import com.github.muneebwanee.dash.R
import com.github.muneebwanee.dash.data.model.Calls
import com.github.muneebwanee.dash.utils.ConstFun.runThread
import com.github.muneebwanee.dash.utils.ConstFun.convertCurrentDuration
import com.pawegio.kandroid.longToast
import kotterknife.bindView
import java.io.File
import java.io.FileNotFoundException
import android.media.AudioManager
import com.github.muneebwanee.dash.utils.Consts.TYPE_CALL_INCOMING
import com.github.muneebwanee.dash.utils.Consts.TYPE_CALL_OUTGOING
import com.pawegio.kandroid.hide
import com.pawegio.kandroid.show
import com.romancha.playpause.PlayPauseView
import de.hdodenhof.circleimageview.CircleImageView
import com.github.muneebwanee.dash.data.preference.DataSharePreference.getSelectedItem


/**
 * Created by muneebwanee on 15/12/20.
 */
class CallsViewHolder(view: View, val context: Context) : RecyclerView.ViewHolder(view) {

    private val contact: TextView by bindView(R.id.contact_item_calls)
    private val phoneNumber: TextView by bindView(R.id.phone_number_item_calls)
    private val dateTime: TextView by bindView(R.id.date_item_calls)
    val imageItem: ImageView by bindView(R.id.img_item_calls)
    val progressCall: CircleProgressView by bindView(R.id.progress_item_calls)
    val itemClick: LinearLayout by bindView(R.id.item_click_calls)
    private val duration: TextView by bindView(R.id.duration_item_calls)
    private val currentDuration: TextView by bindView(R.id.current_duration_item_calls)
    private val imgCallType : ImageView by bindView(R.id.img_type_call)
    val btnPlay: PlayPauseView by bindView(R.id.play_button_calls)
    private val seekBarProgress : SeekBar by bindView(R.id.progress_seekbar_calls)
    private val imgCheck : CircleImageView by bindView(R.id.img_selected_calls)
    private val card : androidx.cardview.widget.CardView by bindView(R.id.cardview_calls)

    private val audio = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    internal var downloader : Boolean = false
    private var mediaPlayer: MediaPlayer? = null
    private var currentProgress = false
    private var runThreads: Thread? = null
    private var playing: Boolean = false
    private var stopPlayer: Boolean = true

    fun isSelectedItem(key:String,file:File){
        if (itemView.context.getSelectedItem(key)){
            imgCheck.show()
            btnPlay.hide()
            imageItem.hide()
            card.setCardBackgroundColor(ContextCompat.getColor(itemView.context,R.color.colorSelected))
        }else{
            card.setCardBackgroundColor(ContextCompat.getColor(itemView.context,R.color.colorWhite))
            imgCheck.hide()
            if (file.exists()) setOnPlay(true) else setOnPlay(false)
        }
    }

    fun bind(item: Calls) {
        progressCall.maxValue = 100f
        contact.text = item.contact
        phoneNumber.text = item.phoneNumber
        dateTime.text = item.dateTime
        duration.text = item.duration
        if (item.type== TYPE_CALL_OUTGOING) imgCallType.setImageResource(R.drawable.ic_made_green_24dp)
        if (item.type== TYPE_CALL_INCOMING) imgCallType.setImageResource(R.drawable.ic_received_blue_24dp)
    }

    fun getPlaying(): Boolean = playing
    fun getStopPlayer(): Boolean = stopPlayer

    fun initializeMediaPlayer(fileName: String) {
        try {
            mediaPlayer = MediaPlayer.create(context, Uri.fromFile(File(fileName)))
            mediaPlayer!!.setOnCompletionListener {
                onStopAudioCall()
            }
        } catch (e: FileNotFoundException) {
            context.longToast(e.message.toString())
        }
    }

    fun onPlayAudioCall() {

        val currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (currentVolume == 0) context.longToast(R.string.volume_up)

        if (mediaPlayer != null) {
            btnPlay.toggle()
            setMaxProgress(mediaPlayer!!.duration)
            mediaPlayer!!.start()
            playing = true
            stopPlayer = false
            currentProgress = true
            setCurrentProgress()

        }
    }

    fun onPauseAudioCall() {
        if (mediaPlayer != null) {
            mediaPlayer!!.pause()
            btnPlay.toggle()
            playing = false
            stopPlayer = false
        }
    }

    fun onStopAudioCall() {
        if (mediaPlayer != null) {
            currentProgress = false
            stopCurrentProgress()
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            mediaPlayer = null
            if (!btnPlay.onPlaying()) btnPlay.toggle()
            setProgressValue(0)
            playing = false
            stopPlayer = true
        }
    }

    fun setOnPlay(onPlay:Boolean) {
        if (onPlay){
            imageItem.hide()
            btnPlay.show()
        }else{
            imageItem.show()
            btnPlay.hide()
        }
    }

    private fun setMaxProgress(value: Int) {
        seekBarProgress.progress = 0
        seekBarProgress.max = value
        seekBarProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seek(seekBarProgress.progress)
            }
        })

    }

    private fun seek(position:Int){
        if (mediaPlayer!=null) mediaPlayer!!.seekTo(position)
    }

    private fun setProgressValue(value: Int) {
        currentDuration.text = convertCurrentDuration(value.toLong())
        seekBarProgress.progress = value
    }

    private fun setCurrentProgress() {
        runThreads = runThread(100) {
            if (currentProgress) if (mediaPlayer != null) setProgressValue(mediaPlayer!!.currentPosition)
        }
        runThreads!!.start()
    }

    private fun stopCurrentProgress() {
        if (runThreads != null) runThreads!!.interrupt()
    }

}