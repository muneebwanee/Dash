package com.github.muneebwanee.dash.data.model

/**
 * Created by muneebwanee on 15/12/20.
 */
class NotificationMessages {

    var text:String ?=null
    var title:String ? =null
    var type:Int?=null
    var dateTime:String?=null
    var nameImage:String?=null
    var urlImage:String?=null

    constructor(){}

    constructor(text:String?,title:String?,type:Int,dateTime:String?,nameImage:String?,urlImage:String?){
        this.text = text
        this.title = title
        this.type = type
        this.dateTime = dateTime
        this.nameImage = nameImage
        this.urlImage = urlImage
    }

}