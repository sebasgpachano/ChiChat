package com.team2.chitchat.data.sesion

import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataUserSession @Inject constructor() : Serializable {
    private var nameUser: String? = null
    var token: String = ""
}