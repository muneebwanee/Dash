package com.github.muneebwanee.dash.data.model

/**
 * Created by muneebwanee on 15/12/20.
 */
class KeyData {

    var keyID: String? = null
    var keyText: String? = null

    constructor()

    constructor(keyId: String, keyText: String) {
        this.keyID = keyId
        this.keyText = keyText
    }

}