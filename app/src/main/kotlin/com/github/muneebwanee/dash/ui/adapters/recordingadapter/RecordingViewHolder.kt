package com.github.muneebwanee.dash.ui.adapters.recordingadapter

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import at.grabner.circleprogress.CircleProgressView
import com.github.muneebwanee.dash.R
import com.github.muneebwanee.dash.data.model.Recording
import com.github.muneebwanee.dash.data.preference.DataSharePreference.getSelectedItem
import com.github.muneebwanee.dash.utils.ConstFun.convertCurrentDuration
import com.github.muneebwanee.dash.utils.ConstFun.runThread
import com.pawegio.kandroid.hide
import com.pawegio.kandroid.longToast
import com.pawegio.kandroid.show
import com.romancha.playpause.PlayPauseView
import de.hdodenhof.circleimageview.CircleImageView
import kotterknife.bindView
import java.io.File
import java.io.FileNotFoundException

/**
 * Created by muneebwanee on 15/12/20.
 */
class RecordingViewHolder(view: View, private val context: Context) : RecyclerView.ViewHolder(view) {

    private val name: TextView by bindView(R.id.name_item_record)
    private val dateTime: TextView by bindView(R.id.date_item_record)
    private val imageItem: ImageView by bindView(R.id.img_item_record)
    val progressRecord: CircleProgressView by bindView(R.id.progress_item_record)
    val itemClick: LinearLayout by bindView(R.id.item_click_record)
    private val duration: TextView by bindView(R.id.duration_item_record)
    private val currentDuration: TextView by bindView(R.id.current_duration_item_record)
    private val seekBarProgress: SeekBar by bindView(R.id.progress_seekbar_record)
    val btnPlay: PlayPauseView by bindView(R.id.play_button_record)
    private val imgCheck : CircleImageView by bindView(R.id.img_selected_record)
    private val card : CardView by bindView(R.id.cardview_record)

    private val audio = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    internal var downloader: Boolean = false
    private var mediaPlayer: MediaPlayer? = null
    private var currentProgress = false
    private var runThreads: Thread? = null
    private var playing: Boolean = false

    private var stopPlayer: Boolean = true

    fun isSelectedItem(key:String,file:File){
        if (itemView.context.getSelectedItem(key)){
            card.setCardBackgroundColor(ContextCompat.getColor(itemView.context,R.color.colorSelected))
            imgCheck.show()
            imageItem.hide()
            btnPlay.hide()
        }else{
            card.setCardBackgroundColor(ContextCompat.getColor(itemView.context,R.color.colorWhite))
            imgCheck.hide()
            if (file.exists()) setOnPlay(true) else setOnPlay(false)
        }
    }

    @SuppressLint("SetTextI18n")
    fun bind(item: Recording) {
        progressRecord.maxValue = 100f
        name.text = "A_${item.nameAudio}"
        dateTime.text = item.dateTime
        duration.text = item.duration
    }

    fun getPlaying(): Boolean = playing
    fun getStopPlayer(): Boolean = stopPlayer

    fun initializeMediaPlayer(fileName: String) {
        try {
            mediaPlayer = MediaPlayer.create(context, Uri.fromFile(File(fileName)))
            mediaPlayer!!.setOnCompletionListener {
                onStopAudioRecord()
            }
        } catch (e: FileNotFoundException) {
            context.longToast(e.message.toString())
        }
    }

    fun onPlayAudioRecord() {

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

    fun onPauseAudioRecord() {
        if (mediaPlayer != null) {
            mediaPlayer!!.pause()
            btnPlay.toggle()
            playing = false
            stopPlayer = false
        }
    }

    fun onStopAudioRecord() {
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

    fun setOnPlay(onPlay: Boolean) {
        if (onPlay) {
            imageItem.hide()
            btnPlay.show()
        } else {
            imageItem.show()
            btnPlay.hide()
        }
    }

    private fun setMaxProgress(value: Int) {
        seekBarProgress.progress = 0
        seekBarProgress.max = value
        seekBarProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seek(seekBarProgress.progress)
            }
        })
    }

    private fun setProgressValue(value: Int) {
        currentDuration.text = convertCurrentDuration(value.toLong())
        seekBarProgress.progress = value
    }

    private fun seek(position: Int) {
        if (mediaPlayer != null) mediaPlayer!!.seekTo(position)
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