package com.github.muneebwanee.dash.data.model


/**
 * Created by muneebwanee on 15/12/20.
 */
class ChildRecording {

    var recordAudio: Boolean? = null
    var timeAudio: Long? = null

    constructor() {}

    constructor(recordAudio: Boolean?, timeAudio: Long?) {
        this.recordAudio = recordAudio
        this.timeAudio = timeAudio
    }

}