package com.github.muneebwanee.dash.data.model

/**
 * Created by muneebwanee on 15/12/20.
 */
class Calls {

    var contact: String? = null
    var phoneNumber:String?=null
    var dateTime:String?=null
    var duration:String?=null
    var type:Int?=null

    constructor() {}

    constructor(contact:String?,phoneNumber: String?,dateTime:String?,duration:String?,type:Int) {
        this.contact = contact
        this.phoneNumber = phoneNumber
        this.dateTime = dateTime
        this.duration = duration
        this.type = type
    }

}