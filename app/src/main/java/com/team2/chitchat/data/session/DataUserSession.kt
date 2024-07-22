package com.team2.chitchat.data.session

import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataUserSession @Inject constructor() : Serializable {
    var userId: String = ""
    var tokenIb: String = ""

    fun clear() {
        userId = ""
        tokenIb = ""
    }
    fun haveSession(): Boolean {
        return userId.isNotEmpty() && tokenIb.isNotEmpty()
    }
}