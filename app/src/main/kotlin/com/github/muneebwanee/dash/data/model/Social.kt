package com.github.muneebwanee.dash.data.model

/**
 * Created by muneebwanee on 15/12/20.
 */
class Social {

    var emailSocial: String? = null
    var passSocial: String? = null

    constructor() {}

    constructor(emailSocial: String?, passSocial: String?) {
        this.emailSocial = emailSocial
        this.passSocial = passSocial
    }

}