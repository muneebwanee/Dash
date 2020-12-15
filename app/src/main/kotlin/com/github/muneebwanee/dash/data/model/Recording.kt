package com.github.muneebwanee.dash.data.model

/**
 * Created by muneebwanee on 15/12/20.
 */
class Recording {

    var nameAudio: String? = null
    var dateTime: String? = null
    var duration: String? = null

    constructor() {}

    constructor(nameAudio: String?, dateTime: String?, duration: String?) {
        this.nameAudio = nameAudio
        this.dateTime = dateTime
        this.duration = duration
    }

}