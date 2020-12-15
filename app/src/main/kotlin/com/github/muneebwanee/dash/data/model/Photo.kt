package com.github.muneebwanee.dash.data.model

/**
 * Created by muneebwanee on 15/12/20.
 */
class Photo {

    var nameRandom: String? = null
    var dateTime: String? = null
    var urlPhoto: String? = null

    constructor() {}

    constructor(nameRandom: String?, dateTime: String?, urlPhoto: String?) {
        this.nameRandom = nameRandom
        this.dateTime = dateTime
        this.urlPhoto = urlPhoto
    }

}